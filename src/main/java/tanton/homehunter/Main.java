package tanton.homehunter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import tanton.homehunter.config.Config;
import tanton.homehunter.domain.hibernate.Listing;
import tanton.homehunter.domain.hibernate.SearchProfile;
import tanton.homehunter.dynamo.DynamoController;
import tanton.homehunter.mail.Mailer;
import tanton.homehunter.util.HibernateUtil;
import tanton.homehunter.util.HtmlStringBuilder;
import tanton.homehunter.util.HttpClient;
import tanton.homehunter.zoopla.ZooplaFetcher;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {


    public static void main(final String[] args) throws IOException, MessagingException {
        final Config config = getGson().fromJson(readFile("config.json", UTF_8), Config.class);

        final Mailer mailer = new Mailer(config.getEmailConfig());


        final SessionFactory factory = HibernateUtil.getSessionFactory();
        final Session readSession = factory.openSession();
        final Session writeSession = factory.openSession();


        final HttpClient httpClient = new HttpClient();

        StringBuilder snsOut = new StringBuilder();

        HtmlStringBuilder html = new HtmlStringBuilder();
        html.appendOpenTag("html").appendOpenTag("head").appendCloseTag("head").appendOpenTag("body")
                .indent().appendOpenTag("h1").append("Property Results").appendCloseTag("h1");

        final List<SearchProfile> searchProfiles = getSearchProfiles(readSession);

        final ZooplaFetcher zooplaFetcher = new ZooplaFetcher(getGson(), httpClient);

        final List<Listing> listings = new ArrayList<>();

        searchProfiles.forEach(sp -> {
            try {
                listings.addAll(zooplaFetcher.getAll(sp.getProfilePostCode(), sp.getProfileRadius(), sp.getMaxPrice()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        processResultHibernate(listings, readSession, writeSession, html);
//        processResultDynamo(listings, new DynamoController(), html);


        readSession.close();
        writeSession.close();
        factory.close();
//        if (snsOut.toString().length() > 0)
//            sns.publish(SNS_ARN, snsOut.toString(), "Property sales");


        FileWriter fw = new FileWriter(new File("out.html"));
        final String output = html.toString();
        fw.write(output);
        fw.close();

        sendMail(mailer, "An update is available", output);




    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static void sendMail(final Mailer mailer, final String subject, final String msg) throws MessagingException {

        mailer.sendMessage("pete.tanton@streamingrocket.com", subject, msg);

    }


//    private static void processResultDynamo(final List<Listing> listings, final DynamoController dynamo, final HtmlStringBuilder html) {
//        for (Listing listing : listings) {
//        }
//
//    }

    private static void processResultHibernate(final List<Listing> listings, final Session readSession, final Session writeSession, final HtmlStringBuilder html) {
//        listings.removeIf(listingIdBlackList::contains);
        final Transaction readTx = readSession.beginTransaction();
        final Transaction writeTx = writeSession.beginTransaction();
        for (Listing listing : listings) {
            Query q = readSession.createQuery("from Listing where listing_id = :lid");
            q.setParameter("lid", listing.getListingId());
            final List list = q.list();
            if (list.size() > 0) {
                final Listing l = (Listing) list.get(0);
//                System.out.println(String.format("{ %s : %s", l.getFirstPublishedDate().getTime(), listing.getFirstPublishedDate().getTime()));
                if (l.getFirstPublishedDate().compareTo(listing.getFirstPublishedDate()) != 0 || l.getLastPublishedDate().compareTo(listing.getLastPublishedDate()) != 0) {
                    html.appendListing(listing, HtmlStringBuilder.Reason.UPDATE);
//                    notificationService.putListing(listing, NotificationService.Reason.UPDATE);
                    listing.setId(l.getId());

                    writeSession.saveOrUpdate(listing.getListingId(), listing);
//                    session.saveOrUpdate(String.valueOf(listing.getId()), listing);
                }
            } else {
                html.appendListing(listing, HtmlStringBuilder.Reason.NEW);

//                notificationService.putListing(listing, NotificationService.Reason.NEW);
                writeSession.save(listing.getListingId(), listing);

            }

        }
        readTx.commit();
        writeTx.commit();
    }

    private static List<SearchProfile> getSearchProfiles(final Session session) {
        final Transaction tx = session.beginTransaction();

        final Query query = session.createQuery("from SearchProfile");
        final List list = query.list();

        final List<SearchProfile> searchProfiles = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            searchProfiles.add((SearchProfile) list.get(i));
        }

        return searchProfiles;


    }


    private static Gson getGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }
}
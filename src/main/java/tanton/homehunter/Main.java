package tanton.homehunter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tanton.homehunter.aws.S3Controller;
import tanton.homehunter.config.Config;
import tanton.homehunter.domain.dynamo.Listing;
import tanton.homehunter.domain.dynamo.SearchProfile;
import tanton.homehunter.aws.DynamoController;
import tanton.homehunter.mail.Mailer;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {


    public static void main(final String[] args) throws IOException, MessagingException {
        final Config config = getGson().fromJson(readFile("config.json", UTF_8), Config.class);

        final Mailer mailer = new Mailer(config.getEmailConfig());
        final DynamoController dynamo = new DynamoController();
        final S3Controller s3 = new S3Controller();


        final HttpClient httpClient = new HttpClient();

        HtmlStringBuilder html = new HtmlStringBuilder();
        html.appendOpenTag("html").appendOpenTag("head").appendCloseTag("head").appendOpenTag("body")
                .indent().appendOpenTag("h1").append("Property Results").appendCloseTag("h1").appendOpenTag("ul");

        final List<SearchProfile> searchProfiles = dynamo.getAllSearchProfiles();

        final ZooplaFetcher zooplaFetcher = new ZooplaFetcher(getGson(), httpClient);

        final List<Listing> listings = new ArrayList<>();

        searchProfiles.forEach(sp -> {
            try {
                listings.addAll(zooplaFetcher.getAll(sp.getProfilePostCode(), sp.getProfileRadius(), sp.getMaxPrice()));
                listings.removeIf(l -> sp.getExcludedListings().contains(l.getListingId()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        final boolean shouldPublish = processResultDynamo(listings, dynamo, html);

        html.appendCloseTag("ul").appendCloseTag("body").appendCloseTag("html");

        final String fileName = String.valueOf(Instant.now().getEpochSecond()) + ".html";
        final File file = new File(fileName);
        FileWriter fw = new FileWriter(file);

        final String output = html.toString();
        fw.write(output);
        fw.close();

        if (shouldPublish) {
            s3.putSearch(fileName, file);
            s3.updateIndex();

            sendMail(mailer, "An update is available", "Please visit https://s3-eu-west-1.amazonaws.com/house-hunting/index.html for more info");
        } else {
            System.out.println("no updates to publish");
        }



    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static void sendMail(final Mailer mailer, final String subject, final String msg) throws MessagingException {

        mailer.sendMessage("pete.tanton@streamingrocket.com", subject, msg);

    }


    private static boolean processResultDynamo(final List<Listing> listings, final DynamoController dynamo, final HtmlStringBuilder html) {
        boolean shoudPublish = false;
        html.appendOpenTag("p").append("This list contains updates or new listings").appendCloseTag("p");
        for (Listing listing : listings) {
            final DynamoController.SaveListingStatus saveListingStatus = dynamo.saveListing(listing, "pete.tanton@streamingrocket.com");
            switch (saveListingStatus) {
                case NEW:
                    html.appendListing(listing, HtmlStringBuilder.Reason.NEW);
                    shoudPublish = true;
                    continue;
                case UPDATE:
                    html.appendListing(listing, HtmlStringBuilder.Reason.UPDATE);
                    shoudPublish = true;
                    continue;
                default:
                    System.out.println("no change");
            }
        }
        return shoudPublish;
    }



    private static Gson getGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }
}
package tanton.homehunter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tanton.homehunter.aws.DynamoController;
import tanton.homehunter.aws.S3Controller;
import tanton.homehunter.config.Config;
import tanton.homehunter.config.GoogleConfig;
import tanton.homehunter.domain.dynamo.Listing;
import tanton.homehunter.domain.dynamo.SearchProfile;
import tanton.homehunter.google.CommuteData;
import tanton.homehunter.google.Directions;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {

    private static void google(final GoogleConfig googleConfig) throws Exception {
        final Directions directions = new Directions(googleConfig);
        for (Map.Entry<String, List<CommuteData>> entry : directions.getRouteData("SW9 0LP").entrySet()) {
            System.out.println(entry.getKey());
            for (CommuteData commuteData : entry.getValue()) {
                System.out.println(commuteData.getTravelMode().toString() + ": " + commuteData.getRouteData().getDuration());
            }
            System.out.println("\n\n");
        }
    }

    public static void main(final String[] args) throws Exception {
        final Config config = getGson().fromJson(readFile("config.json", UTF_8), Config.class);
//        google(config.getGoogleConfig());
//        System.exit(1);

        final Mailer mailer = new Mailer(config.getEmailConfig());
        final DynamoController dynamo = new DynamoController();
        final S3Controller s3 = new S3Controller();


        final HttpClient httpClient = new HttpClient();

        HtmlStringBuilder html = new HtmlStringBuilder();
        html.appendOpenTag("html").appendOpenTag("head").append(htmlHead()).appendCloseTag("head").appendOpenTag("body")
                .indent().append("<div class=\"container\">").appendOpenTag("h1").append("Property Results").appendCloseTag("h1");

        final List<SearchProfile> searchProfiles = dynamo.getAllSearchProfiles();

        final ZooplaFetcher zooplaFetcher = new ZooplaFetcher(getGson(), httpClient, config.getZooplaConfig());

        final List<Listing> listings = new ArrayList<>();


        searchProfiles.forEach(sp -> {

            try {
                listings.addAll(zooplaFetcher.getAll(sp.getProfilePostCode(), sp.getProfileRadius(), sp.getMaxPrice()));

                if (sp.getExcludedListings() != null && sp.getExcludedListings().size() > 0) {
                    sp.getExcludedListings().forEach(el -> System.out.println("excluded: " + el));
                    listings.removeIf(l -> sp.getExcludedListings().contains(l.getListingId()));
                }
                listings.removeAll(getBadListings(listings, sp, dynamo));

            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        final boolean shouldPublish = processResultDynamo(listings, dynamo, html, new Directions(config.getGoogleConfig()));

        html.appendCloseTag("div").append(htmlBodyLast()).appendCloseTag("body").appendCloseTag("html");

        final String fileName = String.valueOf(Instant.now().getEpochSecond()) + ".html";
        final File file = new File(fileName);
        FileWriter fw = new FileWriter(file);

        final String output = html.toString();
        fw.write(output);
        fw.close();

        if (shouldPublish) {
            s3.putSearch(fileName, file);
            s3.updateIndex();

//            sendMail(mailer, "An update is available", "Please visit https://s3-eu-west-1.amazonaws.com/house-hunting/index.html for more info");
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

    private static String generateCommuteData(final Listing listing, final Directions directions) {
        final StringBuilder sb = new StringBuilder();
        System.out.println(listing.getDisplayableAddress());
        for (Map.Entry<String, List<CommuteData>> entry : directions.getRouteData(listing.getDisplayableAddress()).entrySet()) {
            sb.append("<strong>").append(entry.getKey()).append("</strong>");
            for (CommuteData commuteData : entry.getValue()) {
                sb.append("</br>").append(commuteData.getTravelMode().toString()).append(": ").append(commuteData.getRouteData().getDuration()).append("</br>");
            }
        }
        return sb.toString();
    }


    private static boolean processResultDynamo(final List<Listing> listings, final DynamoController dynamo, final HtmlStringBuilder html, final Directions directions) {
        final Map<String, String> typeContentMap = new HashMap<>();
        boolean shoudPublish = false;
        html.appendOpenTag("p").append("This list contains updates or new listings").appendCloseTag("p");
        for (Listing listing : listings) {
            final DynamoController.SaveListingStatus saveListingStatus = dynamo.saveListing(listing, "pete.tanton@streamingrocket.com");
            System.out.println("property type: " + listing.getPropertyType());
            final StringBuilder sb = new StringBuilder();
            switch (saveListingStatus) {
                case NEW:
                    if (typeContentMap.containsKey(listing.getPropertyType())) {
                        sb.append(typeContentMap.get(listing.getPropertyType()));
                        sb.append(generateListingString(listing, HtmlStringBuilder.Reason.NEW));
                        sb.append("<p>").append(generateCommuteData(listing, directions)).append("</p>");
                        typeContentMap.replace(listing.getPropertyType(), sb.toString());
                    } else {
                        sb.append(generateListingString(listing, HtmlStringBuilder.Reason.NEW));
                        sb.append("<p>").append(generateCommuteData(listing, directions)).append("</p>");
                        typeContentMap.put(listing.getPropertyType(), sb.toString());
                    }
                    shoudPublish = true;
                    continue;
                case UPDATE:
                    if (typeContentMap.containsKey(listing.getPropertyType())) {
                        sb.append(typeContentMap.get(listing.getPropertyType()));
                        sb.append(generateListingString(listing, HtmlStringBuilder.Reason.UPDATE));
                        sb.append("<p>").append(generateCommuteData(listing, directions)).append("</p>");
                        typeContentMap.replace(listing.getPropertyType(), sb.toString());
                    } else {
                        sb.append(generateListingString(listing, HtmlStringBuilder.Reason.UPDATE));
                        sb.append("<p>").append(generateCommuteData(listing, directions)).append("</p>");
                        typeContentMap.put(listing.getPropertyType(), sb.toString());
                    }
                    shoudPublish = true;
                    continue;
                default:
                    System.out.println("no change");
            }
        }


        html.append("<ul id=\"navigation\" class=\"nav nav-tabs\">");
        typeContentMap.forEach((s, s2) -> html.appendOpenTag("li").append("<a data-toggle=\"tab\" href=\"#").append(s.toLowerCase().replace(" ", "-")).append("\">").append(s).appendCloseTag("a").appendCloseTag("li"));
        html.appendCloseTag("ul");
        html.append("<div class=\"tab-content\">");
        typeContentMap.forEach((s, s2) -> html.append("<div id=\"").append(s.toLowerCase().replace(" ", "-")).append("\" class=\"tab-pane fade\">").appendOpenTag("ul").append(s2).appendCloseTag("ul").appendCloseTag("div"));
        html.appendCloseTag("div");
        return shoudPublish;
    }

    private static String generateListingString(final Listing listing, final HtmlStringBuilder.Reason reason) {
        final StringBuilder sb = new StringBuilder();
        sb
                .append("<li><h2><a href=\"").append(listing.getDetailsUrl()).append("\">[").append(reason.name()).append("] { £").append(listing.getPrice()).append(" } ").append(listing.getPropertyType()).append(" ").append(listing.getNumBedrooms()).append(" bed(s)</a></h2>")
                .append("<p>").append(listing.getShortDescription()).append("</p>")
                .append("<img src=\"").append(listing.getImageUrl()).append("\"></li>\n");
        return sb.toString();
    }


    private static Gson getGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }

    public static final List<Listing> getBadListings(final List<Listing> listings, final SearchProfile searchProfile, final DynamoController dynamo) {
        final Set<String> badDescriptionStrings = new HashSet<>();
        final List<String> excludedListings;
        if (null == searchProfile.getExcludedListings()) {
            excludedListings = new ArrayList<>();
        } else {
            excludedListings = searchProfile.getExcludedListings();

        }
        badDescriptionStrings.add("Homewise's lifetime lease plan");
        badDescriptionStrings.add("Over 55's only");
        badDescriptionStrings.add("retirement");
        badDescriptionStrings.add("Auction sale");

        final List<Listing> badListings = new ArrayList<>();

        for (Listing l : listings) {
            boolean badListing = false;
            for (final String s : badDescriptionStrings) {
                if (l.getShortDescription().toLowerCase().contains(s.toLowerCase())) {
                    if (!excludedListings.contains(l.getListingId()))
                        excludedListings.add(l.getListingId());
                    System.out.println("excluding: " + l.getListingId());
                    badListing = true;
                }
            }
            if (badListing)
                badListings.add(l);
        }

        searchProfile.setExcludedListings(excludedListings);
        dynamo.saveSearchProfile(searchProfile);

        return badListings;
    }

    private static String htmlHead() {
        final StringBuilder sb = new StringBuilder();
        sb
                .append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
                .append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js\"></script>")
                .append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>")
                .append("<meta charset=\"utf-8\">");
        return sb.toString();
    }

    private static String htmlBodyLast() {
        final StringBuilder sb = new StringBuilder();
        sb
                .append("<script>var tabContent=document.getElementsByClassName('tab-content')[0];")
                .append("tabContent.childNodes[0].className = tabContent.childNodes[0].className + ' in active';")
                .append("var nav = document.getElementsByClassName('nav')[0];")
                .append("nav.childNodes[0].className = 'active';")
                .append("</script>");
        return sb.toString();
    }
}
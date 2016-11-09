package tanton.homehunter.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import tanton.homehunter.util.HtmlStringBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class S3Controller {

    private final AmazonS3Client s3client;

    public S3Controller() {
        this.s3client = new AmazonS3Client(new ProfileCredentialsProvider("home-hunter"), new ClientConfiguration());
        this.s3client.setRegion(Region.getRegion(Regions.EU_WEST_1));
    }

    public void putSearch(final String filename, final File file) throws IOException {
        s3client.putObject(new PutObjectRequest("house-hunting", "updates/" + filename, file).withCannedAcl(CannedAccessControlList.PublicRead));



    }

    public void updateIndex() throws IOException {
        final ObjectListing objects = s3client.listObjects("house-hunting", "updates/");
        final HtmlStringBuilder html = new HtmlStringBuilder();
        html
                .appendOpenTag("html").appendOpenTag("head").appendDefaultStyles().appendCloseTag("head")
                .appendOpenTag("body")
                .appendOpenTag("h1").append("Housing Listings (powered by the Zoopla API)").appendCloseTag("h1")
                .appendOpenTag("p").append("Results are built from custom searches and provide links to the full adds on the Zoopla website. These search results are deleted after 28 days.").appendCloseTag("p")
                .appendOpenTag("p");


        for (S3ObjectSummary summary : objects.getObjectSummaries()) {
            try {
                final String key = summary.getKey();
                final Long seconds = Long.parseLong(key.substring(key.lastIndexOf("/") + 1, key.lastIndexOf("."))) * 1000L;
                final Date date = new Date(seconds);

                html.append("<a href=\"").append(key).append("\">").append(date.toString()).appendCloseTag("a").appendCloseTag("br");
            } catch (Exception e) {
                System.out.println("Issue parsing: " + summary.getKey());
            }
        }

        html
                .appendCloseTag("p")
                .newLine()
                .appendOpenTag("body").appendCloseTag("html");

        final File indexFile = new File("index.html");
        final FileWriter indexFw = new FileWriter(indexFile);
        indexFw.write(html.toString());
        indexFw.close();

        s3client.putObject(new PutObjectRequest("house-hunting", "index.html", indexFile).withCannedAcl(CannedAccessControlList.PublicRead));
    }
}

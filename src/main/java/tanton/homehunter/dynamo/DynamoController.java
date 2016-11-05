package tanton.homehunter.dynamo;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import tanton.homehunter.domain.dynamo.DListing;

public class DynamoController {
    private final DynamoDBMapper mapper;

    public DynamoController() {
        final AmazonDynamoDBAsyncClient dynamoDB = new AmazonDynamoDBAsyncClient(new ProfileCredentialsProvider("home-hunter"), new ClientConfiguration());
        dynamoDB.setRegion(Region.getRegion(Regions.EU_WEST_1));
        mapper = new DynamoDBMapper(dynamoDB);
    }

    public DListing getAllListings(final String username) {
        return mapper.load(DListing.class, username);
    }

    public void saveListing(final DListing listing, final String username) {
        listing.setUsername(username);
        mapper.save(listing);
    }
}

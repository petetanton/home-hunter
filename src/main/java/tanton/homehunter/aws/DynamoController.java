package tanton.homehunter.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import tanton.homehunter.domain.dynamo.Listing;
import tanton.homehunter.domain.dynamo.SearchProfile;
import tanton.homehunter.domain.dynamo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DynamoController {
    private final DynamoDBMapper mapper;

    private static final int BACKOFF = 2000;
    private static final int MAX_BACKOFF = 60000;

    public DynamoController() {
        final AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient(new ProfileCredentialsProvider("home-hunter"), new ClientConfiguration());
        dynamoDB.setRegion(Region.getRegion(Regions.EU_WEST_1));
        mapper = new DynamoDBMapper(dynamoDB);
    }

    public Listing getAllListings(final String username) {
        return mapper.load(Listing.class, username);
    }

    public SaveListingStatus saveListing(final Listing newListing, final String username) {
        SaveListingStatus status;
        final Optional<Listing> currentListing = getListing(newListing.getListingId(), username);


        if (currentListing.isPresent()) {
            if (currentListing.get().getLastPublishedDate().equals(newListing.getLastPublishedDate())) {
                return SaveListingStatus.NO_CHANGE;
            }
            status = SaveListingStatus.UPDATE;
        } else {
            status = SaveListingStatus.NEW;
        }

        System.out.println("saving: " + newListing.getListingId());
        newListing.setUsername(username);
        saveListingWithBackoff(newListing, BACKOFF);
        return status;
    }

    private Optional<Listing> getListing(final String listingId, final String username) {
        return Optional.ofNullable(mapper.load(Listing.class, listingId, username));

    }

    public List<SearchProfile> getAllSearchProfiles() {
        final List<SearchProfile> response = new ArrayList<>();
        final PaginatedScanList<SearchProfile> scan = mapper.scan(SearchProfile.class, new DynamoDBScanExpression());
        response.addAll(scan.stream().collect(Collectors.toList()));
        return response;
    }

    public List<User> getAllUsers() {
        return mapper.scan(User.class, new DynamoDBScanExpression());
    }

    private void saveListingWithBackoff(final Listing listing, final int backoff) {
        try {
            this.mapper.save(listing);
        } catch (ProvisionedThroughputExceededException e) {
            System.out.println("provisioned through put exceeded - we are going into backoff mode for " + backoff + "ms");
            try {
                Thread.sleep(backoff);
                saveListingWithBackoff(listing, getBackoff(backoff + 100));
            } catch (InterruptedException ignored) {
            }
        }
    }

    private int getBackoff(int backoff) {
        if (backoff > MAX_BACKOFF)
            return MAX_BACKOFF;
        return backoff;
    }

    private void saveSearchProfile(final SearchProfile searchProfile) {
        this.mapper.save(searchProfile);
    }

    public enum SaveListingStatus {
        NO_CHANGE,
        UPDATE,
        NEW
    }

}

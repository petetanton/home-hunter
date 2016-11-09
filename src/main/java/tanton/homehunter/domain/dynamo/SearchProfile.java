package tanton.homehunter.domain.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Date;
import java.util.List;

@DynamoDBTable(tableName = "home_search_profile")
public class SearchProfile {

    @DynamoDBHashKey(attributeName = "profile_id")
    private int profileId;

    @DynamoDBRangeKey(attributeName = "profile_account")
    private String username;
    private String profileType;

    @DynamoDBAttribute(attributeName = "profile_post_code")
    private String profilePostCode;

    @DynamoDBAttribute(attributeName = "profile_radius")
    private String profileRadius;
    private String minBeds;
    private String propertyTypeBlacklist;

    @DynamoDBAttribute(attributeName = "profile_max_price")
    private String maxPrice;
    private String notes;

    @DynamoDBAttribute(attributeName = "profile_exclude_listings")
    private List<String> excludedListings;

    public List<String> getExcludedListings() {
        return excludedListings;
    }

    public void setExcludedListings(List<String> excludedListings) {
        this.excludedListings = excludedListings;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getMinBeds() {
        return minBeds;
    }

    public void setMinBeds(String minBeds) {
        this.minBeds = minBeds;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getProfilePostCode() {
        return profilePostCode;
    }

    public void setProfilePostCode(String profilePostCode) {
        this.profilePostCode = profilePostCode;
    }

    public String getProfileRadius() {
        return profileRadius;
    }

    public void setProfileRadius(String profileRadius) {
        this.profileRadius = profileRadius;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getPropertyTypeBlacklist() {
        return propertyTypeBlacklist;
    }

    public void setPropertyTypeBlacklist(String propertyTypeBlacklist) {
        this.propertyTypeBlacklist = propertyTypeBlacklist;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(final String notes) {
        this.notes = String.format("%s (at %s)", notes, new Date().toString());
    }
}

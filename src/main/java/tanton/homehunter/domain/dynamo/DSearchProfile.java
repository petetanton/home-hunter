package tanton.homehunter.domain.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "home_search_profile")
public class DSearchProfile {

    @DynamoDBHashKey
    private int profileId;

    @DynamoDBRangeKey(attributeName = "profile_account")
    private String username;
    private String profileType;
    private String profilePostCode;
    private String profileRadius;
    private String minBeds;
    private String propertyTypeBlacklist;
    private String maxPrice;

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
}

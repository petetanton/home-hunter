package tanton.homehunter.domain.hibernate;

public class SearchProfile {

    private int profileId;
    private String profileType;
    private String profilePostCode;
    private String profileRadius;
    private String minBeds;
    private String propertyTypeBlacklist;
    private String maxPrice;

    public String getMaxPrice() {
        return maxPrice;
    }

    public String getMinBeds() {
        return minBeds;
    }

    public int getProfileId() {
        return profileId;
    }

    public String getProfilePostCode() {
        return profilePostCode;
    }

    public String getProfileRadius() {
        return profileRadius;
    }

    public String getProfileType() {
        return profileType;
    }

    public String getPropertyTypeBlacklist() {
        return propertyTypeBlacklist;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setMinBeds(String minBeds) {
        this.minBeds = minBeds;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public void setProfilePostCode(String profilePostCode) {
        this.profilePostCode = profilePostCode;
    }

    public void setProfileRadius(String profileRadius) {
        this.profileRadius = profileRadius;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public void setPropertyTypeBlacklist(String propertyTypeBlacklist) {
        this.propertyTypeBlacklist = propertyTypeBlacklist;
    }
}
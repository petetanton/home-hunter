package tanton.homehunter.domain.hibernate;

import com.google.gson.annotations.SerializedName;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class Listing {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SerializedName(value = "image_caption")
    private String imageCaption;
    private String status;

    @SerializedName(value = "num_floors")
    private String numFloors;

    @SerializedName(value = "listing_status")
    private String listingStatus;

    @SerializedName("num_bedrooms")
    private String numBedrooms;

    @SerializedName(value = "agent_name")
    private String agentName;
    private double latitude;

    @SerializedName(value = "agent_address:")
    private String agentAddress;

    @SerializedName(value = "num_recepts")
    private String numRecepts;

    @SerializedName(value = "property_type")
    private String propertyType;
    private String country;
    private double longitude;

    @SerializedName(value = "first_published_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstPublishedDate;

    @SerializedName(value = "displayable_address")
    private String displayableAddress;

    @SerializedName(value = "price_modifier")
    private String priceModifier;

    @SerializedName(value = "num_bathrooms")
    private String numBathrooms;

    @SerializedName(value = "thumbnail_url")
    private String thumbnailUrl;
    private String description;

    @SerializedName(value = "post_town")
    private String postTown;

    @SerializedName(value = "details_url")
    private String detailsUrl;

    @SerializedName(value = "agent_logo")
    private String agentLogo;

    @SerializedName(value = "short_description")
    private String shortDescription;

    @SerializedName(value = "agent_phone")
    private String agentPhone;
    private String outcode;

    @SerializedName(value = "image_url")
    private String imageUrl;

    @SerializedName(value = "last_published_date")
    private Date lastPublishedDate;
    private String county;
    private String price;

    @SerializedName(value = "listing_id")
    private String listingId;

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getAgentLogo() {
        return agentLogo;
    }

    public void setAgentLogo(String agentLogo) {
        this.agentLogo = agentLogo;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    public String getDisplayableAddress() {
        return displayableAddress;
    }

    public void setDisplayableAddress(String displayableAddress) {
        this.displayableAddress = displayableAddress;
    }

    public Date getFirstPublishedDate() {
        return firstPublishedDate;
    }

    public void setFirstPublishedDate(Date firstPublishedDate) {
        this.firstPublishedDate = firstPublishedDate;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getLastPublishedDate() {
        return lastPublishedDate;
    }

    public void setLastPublishedDate(Date lastPublishedDate) {
        this.lastPublishedDate = lastPublishedDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getListingStatus() {
        return listingStatus;
    }

    public void setListingStatus(String listingStatus) {
        this.listingStatus = listingStatus;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNumBathrooms() {
        return numBathrooms;
    }

    public void setNumBathrooms(String numBathrooms) {
        this.numBathrooms = numBathrooms;
    }

    public String getNumBedrooms() {
        return numBedrooms;
    }

    public void setNumBedrooms(String numBedrooms) {
        this.numBedrooms = numBedrooms;
    }

    public String getNumFloors() {
        return numFloors;
    }

    public void setNumFloors(String numFloors) {
        this.numFloors = numFloors;
    }

    public String getNumRecepts() {
        return numRecepts;
    }

    public void setNumRecepts(String numRecepts) {
        this.numRecepts = numRecepts;
    }

    public String getOutcode() {
        return outcode;
    }

    public void setOutcode(String outcode) {
        this.outcode = outcode;
    }

    public String getPostTown() {
        return postTown;
    }

    public void setPostTown(String postTown) {
        this.postTown = postTown;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceModifier() {
        return priceModifier;
    }

    public void setPriceModifier(String priceModifier) {
        this.priceModifier = priceModifier;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}


//        "price_change":[{"direction": "", "date": "2016-06-17 13:28:05", "percent": "0%",…],

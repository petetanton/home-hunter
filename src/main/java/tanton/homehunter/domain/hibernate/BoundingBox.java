package tanton.homehunter.domain.hibernate;

public class BoundingBox {
    
    private String longitudeMin;
    private String latitudeMin;
    private String longitudeMax;
    private String latitudeMax;

    public String getLatitudeMax() {
        return latitudeMax;
    }

    public void setLatitudeMax(String latitudeMax) {
        this.latitudeMax = latitudeMax;
    }

    public String getLatitudeMin() {
        return latitudeMin;
    }

    public void setLatitudeMin(String latitudeMin) {
        this.latitudeMin = latitudeMin;
    }

    public String getLongitudeMax() {
        return longitudeMax;
    }

    public void setLongitudeMax(String longitudeMax) {
        this.longitudeMax = longitudeMax;
    }

    public String getLongitudeMin() {
        return longitudeMin;
    }

    public void setLongitudeMin(String longitudeMin) {
        this.longitudeMin = longitudeMin;
    }
}

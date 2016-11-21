package tanton.homehunter.config;

import tanton.homehunter.config.google.Place;

import java.util.List;

public class GoogleConfig {

    private String apiKey;
    private List<Place> places;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}

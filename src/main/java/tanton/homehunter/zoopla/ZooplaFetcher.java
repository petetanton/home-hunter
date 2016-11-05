package tanton.homehunter.zoopla;

import com.google.gson.Gson;
import tanton.homehunter.domain.hibernate.Listing;
import tanton.homehunter.domain.hibernate.PropertyListingResponse;
import tanton.homehunter.util.HttpClient;
import tanton.homehunter.util.HttpResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class ZooplaFetcher {

    private static final String BASE_URL = "http://api.zoopla.co.uk/api/v1/property_listings.js?api_key=b9bhtu9wt2dtyaec4scvzmar&page_size=100&minimum_beds=2&listing_status=sale";


    private final Gson gson;
    private final HttpClient httpClient;

    public ZooplaFetcher(final Gson gson, final HttpClient httpClient) {
        this.gson = gson;
        this.httpClient = httpClient;
    }

    public List<Listing> getAll(final String postCode, final String radius, final String maxPrice) throws IOException {

        final String getUrl = String.format("%s&postcode=%s&radius=%s&maximum_price=%s", BASE_URL, postCode, radius, maxPrice);

        final List<Listing> listings = getPropertyListings(getUrl, 1);

        if (listings.size() != 0) {
            listings.removeIf(x -> x.getLastPublishedDate().before(Date.from(Instant.now().minusMillis(1000 * 60 * 60 * 24 * 7))));
        }

        return listings;
    }


    private List<Listing> getPropertyListings(final String url, int page) throws IOException {
        final String urlStr = url + "&page_number=" + page;
        final HttpResponse httpResponse = httpClient.getWithRetry(urlStr, 0);
        if (httpResponse.getStatusCode() == 200) {
            final PropertyListingResponse propertyListingResponse = gson.fromJson(httpResponse.getBody(), PropertyListingResponse.class);
            if (propertyListingResponse.getListing().size() == 100) {
                List<Listing> response = propertyListingResponse.getListing();
                page++;
                response.addAll(getPropertyListings(url, page));
                return response;
            } else {
                return propertyListingResponse.getListing();
            }
        } else {
            throw new IOException("Returned " + httpResponse.getStatusCode() + " for " + url);
        }
    }
}

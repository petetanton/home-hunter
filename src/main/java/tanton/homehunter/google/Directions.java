package tanton.homehunter.google;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.NotFoundException;
import com.google.maps.errors.OverDailyLimitException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import org.joda.time.DateTime;
import tanton.homehunter.config.GoogleConfig;
import tanton.homehunter.config.google.Place;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Directions {


    private final GoogleConfig googleConfig;
    private final GeoApiContext context;
    private final CommuteDataCache cache;

    private boolean shouldHitGoogle;


    public Directions(final GoogleConfig googleConfig, final Gson gson) throws IOException {
        this.googleConfig = googleConfig;
        this.context = new GeoApiContext().setApiKey(googleConfig.getApiKey());
        this.shouldHitGoogle = true;
        this.cache = new CommuteDataCache(gson, new File(".cache"));
    }

    public Map<String, List<CommuteData>> getRouteData(final String location) throws OverDailyLimitException, IOException {
        Map<String, List<CommuteData>> routeData = new HashMap<>();

        for (Place place : googleConfig.getPlaces()) {
            routeData.put(place.getName(), ImmutableList.of(new CommuteData(getFastestRoute(TravelMode.TRANSIT, location, place.getLocation()), TravelMode.TRANSIT), new CommuteData(getFastestRoute(TravelMode.DRIVING, location, place.getLocation()), TravelMode.DRIVING)));
        }
        return routeData;
    }

    public RouteData getFastestRoute(TravelMode travelMode, String start, String destinaion) throws OverDailyLimitException, IOException {
        if (!shouldHitGoogle) {
            throw new OverDailyLimitException("Over daily limit");
        }
        final DirectionsApiRequest directions = DirectionsApi.getDirections(context, start, destinaion);
        final int dayOfWeek = DateTime.now().getDayOfWeek();
        long addDays = 0;
        if (dayOfWeek >= 5) {
            addDays = 3;
        }

        long addSeconds = 0;
        if (DateTime.now().getSecondOfDay() > 9 * 3600) {
            addSeconds = 3600 * 24 - DateTime.now().getSecondOfDay();
        }


        directions.arrivalTime(org.joda.time.Instant.now().plus(addDays * 24 * 3600 * 1000).plus(addSeconds * 1000));
//        directions.transitMode(TransitMode.BUS, TransitMode.RAIL, TransitMode.SUBWAY, TransitMode.TRAIN, TransitMode.TRAM);
        directions.mode(travelMode);

        final Optional<DirectionsResult> apiResponseFromCache = cache.getApiResponseFromCache(travelMode, start, destinaion);
        final DirectionsResult result;
        if (apiResponseFromCache.isPresent()) {
            result = apiResponseFromCache.get();
        } else {
            result = getResultWithBackOff(directions, 3, 2000);
        }

        if (result == null) {
            return new RouteData(Duration.ofSeconds(0), null);
        } else {
            cache.save(result, travelMode, start, destinaion);
        }

        DirectionsRoute fastestRoute = null;
        long fastestTime = 0;
        for (DirectionsRoute route : result.routes) {
            long duration = 0;
            for (DirectionsLeg leg : route.legs) {
                duration += leg.duration.inSeconds;
            }
            if (duration < fastestTime || fastestTime == 0) {
                fastestRoute = route;
                fastestTime = duration;
            }
        }
        return new RouteData(Duration.ofSeconds(fastestTime), null);
    }

    private DirectionsResult getResultWithBackOff(final DirectionsApiRequest request, int retry, final int backoffTime) throws OverDailyLimitException {
//        System.out.println(String.format("going around {%s, %s}", retry, backoffTime));
        retry--;
        try {

            return request.await();
        } catch (NotFoundException e) {
            System.out.println("Cannot find location");
            return null;
        } catch (OverDailyLimitException e) {
            this.shouldHitGoogle = false;
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

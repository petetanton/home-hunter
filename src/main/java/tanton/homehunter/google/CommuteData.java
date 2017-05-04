package tanton.homehunter.google;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.google.maps.model.TravelMode;

@DynamoDBDocument
public class CommuteData {

    private final TravelMode travelMode;
    private final RouteData routeData;

    public CommuteData(RouteData routeData, TravelMode travelMode) {
        this.routeData = routeData;
        this.travelMode = travelMode;
    }

    public RouteData getRouteData() {
        return routeData;
    }

    public TravelMode getTravelMode() {
        return travelMode;
    }
}

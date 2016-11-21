package tanton.homehunter.google;

import java.time.Duration;
import java.util.List;

public class RouteData {

    private final Duration duration;
    private final List<String> steps;

    public RouteData(Duration duration, List<String> steps) {
        this.duration = duration;
        this.steps = steps;
    }

    public Duration getDuration() {
        return duration;
    }

    public List<String> getSteps() {
        return steps;
    }
}

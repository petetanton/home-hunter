package tanton.homehunter.google;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.errors.OverDailyLimitException;
import org.junit.Test;
import tanton.homehunter.config.Config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DirectionsTest {


    @Test
    public void it() throws IOException, OverDailyLimitException {
        final Config config = getGson().fromJson(readFile("config.json", UTF_8), Config.class);

        final Directions directions = new Directions(config.getGoogleConfig(), new Gson());

        final Map<String, List<CommuteData>> routeData = directions.getRouteData("SW9 0LP");
    }


    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }
}
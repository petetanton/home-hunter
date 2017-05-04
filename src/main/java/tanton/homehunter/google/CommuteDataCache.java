package tanton.homehunter.google;

import com.amazonaws.util.IOUtils;
import com.google.gson.Gson;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class CommuteDataCache {

    private final Gson gson;
    private final File cacheFolder;

    public CommuteDataCache(final Gson gson, final File cacheFolder) throws IOException {
        this.gson = gson;
        this.cacheFolder = cacheFolder;
    }

    public Optional<DirectionsResult> getApiResponseFromCache(final TravelMode travelMode, final String start, final String destinaion) {
        System.out.println("getting from cache");
        final File cacheFile = new File(cacheFolder, generateFileName(travelMode, start, destinaion));
        if (cacheFile.exists()) {
            try {
                final String content = IOUtils.toString(new FileInputStream(cacheFile)).replace("iChronology", "chrono");
                return Optional.of(this.gson.fromJson(content, DirectionsResult.class));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    public void save(final DirectionsResult directionsResult, final TravelMode travelMode, final String start, final String desination) throws IOException {
        System.out.println("saving to cache");
        final File file = new File(cacheFolder, generateFileName(travelMode, start, desination));
        final FileWriter fw = new FileWriter(file, false);
        gson.toJson(directionsResult, fw);
        fw.close();
    }

    private String generateFileName(final TravelMode travelMode, final String start, final String destination) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("SHA-256");
            md5.reset();
            final StringBuilder sb = new StringBuilder();
            final byte[] digest = md5.digest(String.format("%s-%s-%s", travelMode.toString(), start, destination).getBytes(StandardCharsets.UTF_8));

            for (int i=0;i<digest.length;i++) {
                String hex=Integer.toHexString(0xff & digest[i]);
                if(hex.length()==1) sb.append('0');
                sb.append(hex);
            }
            sb.append(".json");
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

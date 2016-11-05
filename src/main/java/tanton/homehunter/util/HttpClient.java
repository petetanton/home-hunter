package tanton.homehunter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;

public class HttpClient {

    private static int SOCKET_TIMEOUT = 8000;

    private long lastRequestTime = Instant.now().getEpochSecond();

    private static void sleep(long time) {
        System.out.println("sleeping for: " + time);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public HttpResponse getWithRetry(String urlStr, long sleepOffset) throws IOException {
        int retryCount = 10;
        IOException e = null;
        while (retryCount > 0) {
            try {
                final HttpResponse httpResponse = get(urlStr);
                if (httpResponse.getStatusCode() == 403) {
                    System.out.println("got a 403");
                    sleep(40000 + sleepOffset);
                    getWithRetry(urlStr, sleepOffset + 10000);
                }
                return httpResponse;
            } catch (IOException t) {
                System.out.println("retrying: " + urlStr);
                e = t;
                retryCount--;
                sleep(10000);
            }
        }
        throw e;

    }

    public HttpResponse get(String urlStr) throws IOException {
        System.out.println("Getting: " + urlStr);
        URL url = new URL(urlStr);


        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(SOCKET_TIMEOUT);
        con.setReadTimeout(SOCKET_TIMEOUT);
        con.setRequestProperty("User-Agent", "london-rent");


        BufferedReader in;
        if (con.getResponseCode() >= 400) {
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }


        String inputLine;

        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append("\n");
        }
        in.close();

        lastRequestTime = Instant.now().getEpochSecond();
        return new HttpResponse(con.getResponseCode(), response.toString(), con.getHeaderFields());
    }


}

package tests.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static tests.config.EnvConfig.BACKEND_URL;

public class BEViaFEStatusSimpleCheck {

    @Test
    public void testBackendApiIsReachable() {
        String targetUrl = BACKEND_URL;
        boolean reachable = false;

        try {
            // Open a connection to the API endpoint
            HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000); // 3-second timeout
            connection.connect();

            // Consider reachable if status code is 2xx or 3xx
            int code = connection.getResponseCode();
            reachable = (code >= 200 && code < 400);
        } catch (IOException e) {
            // If exception occurs, API is considered unreachable
            reachable = false;
        }
        if (!reachable) {
            System.out.println("API is not reachable at " + targetUrl);
        } else {
            System.out.println("API is reachable at " + targetUrl);
        }
        // Assert that the API is reachable
        assertTrue(reachable, "API is not reachable at " + targetUrl);
    }
}

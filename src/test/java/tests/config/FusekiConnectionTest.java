package tests.config;

import org.junit.jupiter.api.*;
import tests.base.BaseRep;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static tests.config.EnvConfig.BACKEND_URL;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FusekiConnectionTest extends BaseRep {

    private HttpClient client;

    @BeforeAll
    public void setup() {
        client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }

    @Test
    public void testHascoapiVersionEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(BACKEND_URL+"/hascoapi/version"))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "API /hascoapi/version should return 200 OK");

        String body = response.body();
        System.out.println("Response body:\n" + body);

        // Verifica se a resposta contém a versão esperada ou partes do HTML
        assertTrue(body.contains("0.8") || body.toLowerCase().contains("version"),
            "Response should contain version information");
    }


}

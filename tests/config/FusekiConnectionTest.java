package tests.config;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FusekiConnectionTest {

    private WebDriver driver;
    private HttpClient client;

    @BeforeAll
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
            "--headless=new",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-gpu",
            "--remote-debugging-port=9222",
            "--window-size=1920,1080",
            "--ignore-certificate-errors"
        );
        driver = new ChromeDriver(options);
        client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }

    @AfterAll
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void testHascoapiToFusekiConnection() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("http://localhost:9000/hascoapi/api/repo/queryTest"))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Hascoapi to Fuseki response: " + response.body());
        assertEquals(200, response.statusCode(), "API should return 200 OK");
        assertTrue(response.body().contains("http") || response.body().contains("result"),
            "Response should contain some triple or result");
    }


    @Test
    public void testYasguiInterfaceLoads() {
        driver.get("http://localhost:8888/");
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("yasgui")));
        assertTrue(driver.getPageSource().contains("YASGUI"), "YASGUI interface should load correctly");
    }
}

package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;

public class RepositoryFormAutomationTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        driver.get("http://localhost/user/login");

        driver.findElement(By.id("edit-name")).sendKeys("admin");
        driver.findElement(By.id("edit-pass")).sendKeys("admin");
        driver.findElement(By.id("edit-submit")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#toolbar-item-user")));
    }

    @AfterEach
    void teardown() {
        // Uncomment this block to close the browser after each test
        // if (driver != null) {
        //     driver.quit();
        // }
    }

    @Test
    void testFillRepositoryForm() throws InterruptedException {
        driver.get("http://localhost/admin/config/rep");

        ensureJwtKeyExists();

        Select jwtDropdown = new Select(driver.findElement(By.cssSelector("select[name='jwt_secret']")));
        jwtDropdown.selectByVisibleText("jwt");

        wait.until(driver -> findInputByLabel("Repository Short Name (ex. \"ChildFIRST\")") != null);

        fillInput("Repository Short Name (ex. \"ChildFIRST\")", "PMSR");
        fillInput("Repository Full Name (ex. \"ChildFIRST: Focus on Innovation\")", "Portuguese Medical Social Repository");
        fillInput("Repository URL (ex: http://childfirst.ucla.edu, http://tw.rpi.edu, etc.)", "https://pmsr.net");
        fillInput("Prefix for Base Namespace (ex: ufmg, ucla, rpi, etc.)", "psmr");
        fillInput("URL for Base Namespace", "https://pmsr.net");
        fillInput("Mime for Base Namespace", "text/turtle");
        fillInput("Source for Base Namespace", "hadatac");
        fillInput("description for the repository that appears in the rep APIs GUI", "pmsr123");
        fillInput("Sagres Base URL", "https://52.214.194.214/");

        String ip = "127.0.0.1";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            System.out.printf("Local IP detected: %s%n", ip);
        } catch (UnknownHostException e) {
            System.out.println("Could not retrieve local IP. Using localhost as fallback.");
        }

        String apiUrl = "http://" + ip + ":9000";
        fillInput("rep API Base URL", apiUrl);

        String expectedFullName = "Portuguese Medical Social Repository";
        boolean formConfirmed = false;

        while (!formConfirmed) {
            WebElement saveBtn = driver.findElement(By.cssSelector("input#edit-submit"));
            saveBtn.click();

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/rep/repo/info"),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".messages--status"))
            ));

            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("/rep/repo/info")) {
                System.out.println("Final page detected: " + currentUrl);
                formConfirmed = true;
            } else {
                // Return to configuration form
                driver.get("http://localhost/admin/config/rep");

                // Refill Repository Full Name if it's empty
                WebElement fullNameField = findInputByLabel("Repository Full Name (ex. \"ChildFIRST: Focus on Innovation\")");
                if (fullNameField != null && fullNameField.getAttribute("value").trim().isEmpty()) {
                    System.out.println("'Repository Full Name' field was empty after saving. Refilling and retrying...");
                    fillInput("Repository Full Name (ex. \"ChildFIRST: Focus on Innovation\")", expectedFullName);
                }

                // Ensure JWT key is selected again
                WebElement jwtSelect = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("select[name='jwt_secret']")));
                jwtDropdown.selectByVisibleText("jwt");
            }
        }
    }

    private void ensureJwtKeyExists() throws InterruptedException {
        WebElement jwtSelect = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("select[name='jwt_secret']")));

        Select jwtDropdown = new Select(jwtSelect);
        boolean jwtExists = jwtDropdown.getOptions().stream()
                .anyMatch(option -> option.getText().trim().equals("jwt"));

        if (!jwtExists) {
            System.out.println("JWT key 'jwt' not found, creating...");

            driver.get("http://localhost/admin/config/system/keys/add");

            wait.until(ExpectedConditions.urlContains("/admin/config/system/keys/add"));
            Thread.sleep(1000); // Ensure rendering

            driver.findElement(By.id("edit-label")).sendKeys("jwt");
            driver.findElement(By.id("edit-description")).sendKeys("jwt");

            new Select(driver.findElement(By.id("edit-key-type")))
                    .selectByValue("authentication");

            new Select(driver.findElement(By.id("edit-key-provider")))
                    .selectByVisibleText("Configuration");

            WebElement valueField;
            try {
                valueField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.id("edit-key-input-settings-key-value")));
            } catch (TimeoutException e) {
                System.out.println("Field 'edit-key-input-settings-key-value' took too long to load, applying fallback...");
                Thread.sleep(2000);
                valueField = driver.findElement(By.id("edit-key-input-settings-key-value"));
            }

            valueField.clear();
            valueField.sendKeys("qwertyuiopasdfghjklzxcvbnm123456");

            driver.findElement(By.id("edit-submit")).click();

            wait.until(ExpectedConditions.urlContains("/admin/config/system/keys"));
            System.out.println("JWT key created successfully.");

            // Return to configuration form
            driver.get("http://localhost/admin/config/rep");
        } else {
            System.out.println("JWT key 'jwt' already exists.");
        }
    }

    private void fillInput(String labelText, String value) {
        WebElement input = findInputByLabel(labelText);
        if (input != null) {
            input.clear();
            input.sendKeys(value);
        } else {
            throw new RuntimeException("Field with label '" + labelText + "' not found.");
        }
    }

    private WebElement findInputByLabel(String labelText) {
        List<WebElement> labels = driver.findElements(By.tagName("label"));
        for (WebElement label : labels) {
            if (label.getText().trim().equals(labelText)) {
                String forAttr = label.getAttribute("for");
                if (forAttr != null && !forAttr.isEmpty()) {
                    try {
                        return driver.findElement(By.id(forAttr));
                    } catch (NoSuchElementException ignored) {}
                }
            }
        }
        return null;
    }
}

package tests.repository;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import tests.base.BaseRep;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static tests.config.EnvConfig.*;

public class RepositoryFormAutomationTest extends BaseRep {


    @Test
    void testFillRepositoryForm() throws InterruptedException {
        driver.get(FRONTEND_URL + "/admin/config/rep");
        Thread.sleep(2000);

        ensureJwtKeyExists();

        Select jwtDropdown = new Select(driver.findElement(By.cssSelector("select[name='jwt_secret']")));
        jwtDropdown.selectByVisibleText("jwt");

        Thread.sleep(2000);

        wait.until(driver -> findInputByLabel("Repository Short Name (ex. \"ChildFIRST\")") != null);
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("edit-sagres-conf")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", checkbox);
        Thread.sleep(500);  // garante que o scroll terminou

        if (!checkbox.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
        }
        Thread.sleep(500);

        // Preenchimento dos campos obrigatórios com logs
        fillInput("Repository Short Name (ex. \"ChildFIRST\")", "HADATAC");
        fillInput("Repository Full Name (ex. \"ChildFIRST: Focus on Innovation\")", "HADATAC");
        fillInput("Repository URL (ex: http://childfirst.ucla.edu, http://tw.rpi.edu, etc.)", "https://hadatac.org");
        fillInput("Prefix for Base Namespace (ex: ufmg, ucla, rpi, etc.)", "hadatac");
        fillInput("URL for Base Namespace", "https://hadatac.org/ont/hadatac#");
        fillInput("Mime for Base Namespace", "");
        fillInput("Source for Base Namespace", "");
        fillInput("description for the repository that appears in the rep APIs GUI", "HADATAC");
        fillInput("Sagres Base URL", "https://52.214.194.214/");

        //String ip = "127.0.0.1";
        String ip = "108.129.120.74"; // IP do servidor de testes
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            System.out.printf("Local IP detected: %s%n", ip);
        } catch (UnknownHostException e) {
            System.out.println("Could not retrieve local IP. Using localhost as fallback.");
        }

        String apiUrl = "http://" + ip + ":9000";
        fillInput("rep API Base URL", apiUrl);

        String expectedFullName = "HADATAC";
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
                driver.get(FRONTEND_URL + "/admin/config/rep");

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

            driver.get(FRONTEND_URL + "/admin/config/system/keys/add");

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
            driver.get(FRONTEND_URL + "/admin/config/rep");
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
    @AfterEach
    void teardown() {
        // Uncomment this block to close the browser after each test
        // if (driver != null) {
        //     driver.quit();
        // }
    }
}

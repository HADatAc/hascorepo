package tests_backup;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseTest {
    protected WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        this.driver = new ChromeDriver(options);
    }

    public void authenticate() {
        try {
            driver.get("http://localhost:8081/");
            WebElement loginLink = driver.findElement(By.cssSelector("a.nav-link.nav-link--user-login"));
            loginLink.click();

            WebElement usernameField = driver.findElement(By.id("edit-name"));
            usernameField.sendKeys("admin");
            WebElement passwordField = driver.findElement(By.id("edit-pass"));
            passwordField.sendKeys("admin");
            WebElement loginButton = driver.findElement(By.id("edit-submit"));
            loginButton.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.titleContains("admin"));
        } catch (Exception e) {
            Assertions.fail("Authentication failed: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        if (this.driver != null) {
            this.driver.quit();
        }
    }
}
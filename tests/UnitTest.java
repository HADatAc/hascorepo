package tests;

import java.time.Duration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

public class UnitTest {
    private DriverManager driver;

    @Before
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");

        // Inicializa o ChromeDriver com as opções
        ChromeDriver driver = new ChromeDriver(options);

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
            wait.until(ExpectedConditions.titleContains("admin")); // Wait until the title contains "admin"
    
            // Authentication successful if no exceptions are thrown
        } catch (Exception e) {
            Assert.fail("Authentication failed: " + e.getMessage());
        }
    }    

    public void navigateAndCheckIcon() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Actions actions = new Actions(driver);
            WebElement dropdownMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Manage Study Elements' and contains(text(), 'Study Elements')]")));
            actions.moveToElement(dropdownMenu).click().perform();
            Thread.sleep(1000);

            WebElement subOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Manage SDD Templates' and text()='Manage SDD Templates']")));
            subOption.click();

            wait.until(ExpectedConditions.urlContains("/rep/select/mt/sdd/table/1/9/none"));
            WebElement deleteButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-delete-selected-element")));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            String beforeContent = (String) js.executeScript("return window.getComputedStyle(arguments[0], '::before').getPropertyValue('content');", deleteButton);
            beforeContent = beforeContent.replaceAll("\"", "");

            // Assert with specific message on failure
            Assert.assertEquals("Expected icon content not found in 'deleteButton'", "", beforeContent.trim());
        } catch (Exception e) {
            Assert.fail("Navigate and Check Icon failed: " + e.getMessage());
        }
    }

    @Test
    public void testAddNewInstrument() {
        try {
            authenticate();
            Thread.sleep(2000);

            navigateAndCheckIcon();
            Thread.sleep(2000);

            // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // Actions actions = new Actions(driver);
            // WebElement dropdownMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Manage Instrument Elements' and contains(text(), 'Instrument Element')]")));
            // actions.moveToElement(dropdownMenu).click().perform();
            // Thread.sleep(1000);

            // WebElement subOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Manage Instruments' and text()='Manage Instruments']")));
            // subOption.click();

            // wait.until(ExpectedConditions.urlContains("/sir/select/instrument/1/9"));
            // WebElement addButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-add-element")));
            // addButton.click();

            // wait.until(ExpectedConditions.urlContains("/sir/manage/addinstrument"));
            // WebElement nameField = driver.findElement(By.id("edit-instrument-name"));
            // nameField.sendKeys("Guitarra");
            // WebElement abbreviationField = driver.findElement(By.id("edit-instrument-abbreviation"));
            // abbreviationField.sendKeys("GUI");

            // WebElement saveButton = driver.findElement(By.id("edit-save-submit"));
            // ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveButton);
            // saveButton.click();

            // wait.until(ExpectedConditions.urlContains("/sir/select/instrument/1/9"));
            // WebElement instrumentTable = driver.findElement(By.id("edit-element-table"));
            // WebElement addedInstrument = instrumentTable.findElement(By.xpath("//td[text()='No Instruments found']"));

            // // Assert with message for adding instrument
            // Assert.assertNotNull("Instrument not found in the list", addedInstrument);
        } catch (Exception e) {
            Assert.fail("Add New Instrument failed: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        if (this.driver != null) {
            this.driver.quit();
        }
    }
}
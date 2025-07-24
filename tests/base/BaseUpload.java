package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static tests.config.EnvConfig.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseUpload {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeAll
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(LOGIN_URL);
        driver.findElement(By.id("edit-name")).sendKeys(USERNAME);
        driver.findElement(By.id("edit-pass")).sendKeys(PASSWORD);
        driver.findElement(By.id("edit-submit")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#toolbar-item-user")));
    }

    protected void navigateToUploadPage(String type) {
        String url = UPLOAD_URL + type + "/none/F";
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));
    }

    protected void fillInputByLabel(String label, String value) {
        WebElement input = driver.findElement(By.xpath("//label[contains(text(),'" + label + "')]/following::input[1]"));
        input.sendKeys(value);
    }

    protected void uploadFile(File file) {
        assertTrue(file.exists(), "File does not exist at given path: " + file.getAbsolutePath());

        try {
            WebElement fileInput = driver.findElement(By.cssSelector("input[name='files[mt_filename]']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", fileInput);
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block'; arguments[0].style.opacity=1;", fileInput);

            fileInput.sendKeys(file.getAbsolutePath());

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", fileInput);

            Thread.sleep(1000);
            System.out.println("File uploaded: " + file.getAbsolutePath());

        } catch (Exception e) {
            fail("Failed to upload the file: " + e.getMessage());
        }
    }

    protected void submitFormAndVerifySuccess() {
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Save')]"));
        saveButton.click();

        boolean confirmationAppeared = wait.until(driver ->
                driver.findElements(By.cssSelector(".messages.status, .alert-success")).size() > 0 ||
                        driver.getPageSource().toLowerCase().contains("successfully")
        );

        assertTrue(confirmationAppeared, "No confirmation message found after upload.");
    }

    /**
     * Navega para a página do Semantic Data Dictionary e extrai o URI do primeiro checkbox na tabela.
     * @return String com o URI extraído.

    protected String extractUriFromSDD() {
        driver.get("http://34.245.157.211/sem/select/semanticdatadictionary/1/9");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-element-table")));
        WebElement checkbox = driver.findElement(By.cssSelector("input.form-checkbox.form-check-input"));
        String uri = checkbox.getAttribute("value");
        System.out.println("URI extracted: " + uri);

        return uri;
    }
     */

    @AfterAll
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

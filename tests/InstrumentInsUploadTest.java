package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstrumentInsUploadTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost/user/login");

        driver.findElement(By.id("edit-name")).sendKeys("admin");
        driver.findElement(By.id("edit-pass")).sendKeys("admin");
        driver.findElement(By.id("edit-submit")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#toolbar-item-user")));
    }

    @Test
    @DisplayName("Upload a valid INS file with basic data")
    void shouldUploadINSFileSuccessfully() {
        driver.get("http://localhost/rep/manage/addmt/ins/none/F");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        // Fill in the "Name" field
        WebElement nameInput = driver.findElement(By.xpath("//label[contains(text(),'Name')]/following::input[1]"));
        nameInput.sendKeys("teste");

        // Fill in the "Version" field
        WebElement versionInput = driver.findElement(By.xpath("//label[contains(text(),'Version')]/following::input[1]"));
        versionInput.sendKeys("1");

        // Upload the INS file !!!!Change the path to your local INS file!!!!
        File insFile = new File("/Users/kael/Downloads/INS-NAMESPACES.xlsx");
        assertTrue(insFile.exists(), "INS file does not exist at given path.");

        try {
            WebElement fileInput = driver.findElement(By.cssSelector("input[name='files[mt_filename]']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", fileInput);
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block'; arguments[0].style.opacity=1;", fileInput);

            fileInput.sendKeys(insFile.getAbsolutePath());

            // Trigger the 'change' event expected by JavaScript (e.g., for auto upload)
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", fileInput);

            System.out.println("File successfully sent to input field: " + insFile.getAbsolutePath());

            Thread.sleep(1000); // Allow time for the file to process

        } catch (Exception e) {
            fail("Failed to upload the file: " + e.getMessage());
        }

        // Click the "Save" button
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Save')]"));
        saveButton.click();

        // Check if a success message is displayed
        boolean confirmationAppeared = wait.until(driver ->
                driver.findElements(By.cssSelector(".messages.status, .alert-success")).size() > 0 ||
                        driver.getPageSource().toLowerCase().contains("successfully")
        );

        assertTrue(confirmationAppeared, "No confirmation message found after upload.");
    }

    @AfterAll
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstrumentInsDeleteTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String insNameToDelete = "teste"; // Same name used in the upload

    @BeforeAll
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("http://localhost/user/login");

        driver.findElement(By.id("edit-name")).sendKeys("admin");
        driver.findElement(By.id("edit-pass")).sendKeys("admin");
        driver.findElement(By.id("edit-submit")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#toolbar-item-user")));
    }

    @Test
    @DisplayName("Delete INS file by name")
    void shouldDeleteINSByName() throws InterruptedException {
        driver.get("http://localhost/rep/select/mt/ins/table/1/9/none");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        boolean foundAndSelected = false;

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 2) {
                String name = cells.get(2).getText().trim(); // "Name" column
                if (insNameToDelete.equalsIgnoreCase(name)) {
                    WebElement checkbox = cells.get(0).findElement(By.cssSelector("input[type='checkbox']"));
                    checkbox.click();
                    foundAndSelected = true;
                    System.out.println("INS found and selected for deletion: " + name);
                    break;
                }
            }
        }

        assertTrue(foundAndSelected, "Could not find INS with name: " + insNameToDelete);

        // Click the "Delete INSs Selected" button
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("edit-delete-selected-element"))
        );
        deleteButton.click();

        // Confirm the JavaScript popup
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        System.out.println("Confirmation popup: " + alert.getText());
        alert.accept();
        System.out.println("Deletion confirmation accepted.");

        // Wait for some indication of success on the page
        wait.until(driver ->
                driver.findElements(By.cssSelector(".messages.status, .alert-success")).size() > 0 ||
                        driver.getPageSource().toLowerCase().contains("deleted")
        );

        System.out.println("INS with name '" + insNameToDelete + "' successfully deleted.");
    }

    @AfterAll
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

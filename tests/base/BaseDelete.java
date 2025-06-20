package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseDelete {

    protected WebDriver driver;
    protected WebDriverWait wait;

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

    protected void deleteFile(String type, String nameToDelete) throws InterruptedException {
        driver.get("http://localhost/rep/select/mt/" + type + "/table/1/9/none");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        boolean foundAndSelected = false;

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 3) {
                String name = cells.get(2).getText().trim();
                if (name.equalsIgnoreCase(nameToDelete)) {
                    WebElement checkbox = cells.get(0).findElement(By.cssSelector("input[type='checkbox']"));
                    checkbox.click();
                    foundAndSelected = true;
                    break;
                }
            }
        }

        assertTrue(foundAndSelected, "Could not find file with name: " + nameToDelete);

        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("edit-delete-selected-element"))
        );
        deleteButton.click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        wait.until(driver ->
                driver.findElements(By.cssSelector(".messages.status, .alert-success")).size() > 0 ||
                        driver.getPageSource().toLowerCase().contains("deleted")
        );
    }

    @AfterAll
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

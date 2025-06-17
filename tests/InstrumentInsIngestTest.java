package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstrumentInsIngestTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final Map<String, Boolean> selectedRows = new HashMap<>();
    private static final int MAX_ATTEMPTS = 10;
    private static final int WAIT_INTERVAL_MS = 30000;

    @BeforeAll
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("http://localhost/user/login");

        driver.findElement(By.id("edit-name")).sendKeys("admin");
        driver.findElement(By.id("edit-pass")).sendKeys("admin");
        driver.findElement(By.id("edit-submit")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#toolbar-item-user")));
    }

    @Test
    @DisplayName("Ingest all unprocessed INS files")
    void shouldIngestAllUnprocessedINSFiles() {
        driver.get("http://localhost/rep/select/mt/ins/table/1/9/none");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        } catch (TimeoutException e) {
            fail("INS table not found on the page.");
        }

        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        int selectedCount = 0;
        System.out.println("Total table rows found: " + rows.size());

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 5) {
                String status = cells.get(4).getText().trim();
                String rowKey = cells.get(1).getText().trim(); // Use unique identifier from column 2

                System.out.println("Row key: " + rowKey + ", status: " + status);

                if ("UNPROCESSED".equalsIgnoreCase(status)) {
                    try {
                        WebElement checkbox = cells.get(0).findElement(By.cssSelector("input[type='checkbox']"));
                        checkbox.click();
                        selectedRows.put(rowKey, true);
                        selectedCount++;
                        System.out.println("Selected UNPROCESSED INS row: " + rowKey);
                    } catch (Exception e) {
                        System.out.println("Failed to select checkbox: " + e.getMessage());
                    }
                }
            }
        }

        if (selectedCount == 0) {
            System.out.println("No UNPROCESSED INS entries found. Test aborted.");
            return;
        }

        System.out.println("Total selected INS entries: " + selectedCount);

        try {
            WebElement ingestButton = driver.findElement(By.id("edit-ingest-mt"));
            ingestButton.click();
            System.out.println("Clicked 'Ingest INS selected as Draft'.");

            wait.until(ExpectedConditions.alertIsPresent());
            Alert confirmAlert = driver.switchTo().alert();
            System.out.println("Alert shown: " + confirmAlert.getText());
            confirmAlert.accept();
            System.out.println("Accepted confirmation alert (clicked 'Yes').");

        } catch (NoSuchElementException e) {
            fail("Ingest button with ID 'edit-ingest-mt' not found.");
        } catch (NoAlertPresentException e) {
            fail("Expected alert not shown after ingest button click.");
        }

        // Retry loop for up to 10 times, every 30 seconds
        int attempts = 0;
        int processedCount = 0;

        while (attempts < MAX_ATTEMPTS) {
            try {
                Thread.sleep(WAIT_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            driver.navigate().refresh();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
            System.out.println("Refreshed page (attempt " + (attempts + 1) + "). Checking statuses...");

            List<WebElement> updatedRows = driver.findElements(By.xpath("//table//tbody//tr"));
            processedCount = 0;

            for (WebElement row : updatedRows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() >= 5) {
                    String rowKey = cells.get(1).getText().trim(); // Use same key as before
                    String newStatus = cells.get(4).getText().trim();

                    if (selectedRows.containsKey(rowKey)) {
                        System.out.println("Row: " + rowKey + " => Status: " + newStatus);
                        if ("PROCESSED".equalsIgnoreCase(newStatus)) {
                            processedCount++;
                        }
                    }
                }
            }

            System.out.println("Processed " + processedCount + " of " + selectedCount + " selected rows.");

            if (processedCount == selectedCount) {
                break; // All processed, exit early
            }

            attempts++;
        }

        assertEquals(selectedCount, processedCount,
                "Mismatch: some selected INS entries were not processed after retries.");
    }

    @AfterAll
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

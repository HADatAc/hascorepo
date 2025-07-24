package tests.base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static tests.config.EnvConfig.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseDelete {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final Map<String, Boolean> selectedRows = new HashMap<>();
    protected static final int MAX_ATTEMPTS = 10;
    protected static final int WAIT_INTERVAL_MS = 10000;
    
    @BeforeAll
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(LOGIN_URL);
        driver.findElement(By.id("edit-name")).sendKeys(USERNAME);
        driver.findElement(By.id("edit-pass")).sendKeys(PASSWORD);
        driver.findElement(By.id("edit-submit")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#toolbar-item-user")));
    }

    protected void deleteFile(String type, String fileName) throws InterruptedException {
        driver.get(FILES_URL + type + "/table/1/9/none");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        } catch (TimeoutException e) {
            fail("Table for type '" + type + "' not found.");
        }

        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        int selectedCount = 0;
        System.out.println("Total table rows found: " + rows.size());

        selectedRows.clear(); // limpar mapa antes

        // Encontrar a linha com fileName e marcar checkbox
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 3) {
                String name = cells.get(2).getText().trim(); // coluna do nome, ajustar se necessário

                if (name.equals(fileName)) {
                    try {
                        WebElement checkbox = cells.get(0).findElement(By.cssSelector("input[type='checkbox']"));
                        if (!checkbox.isSelected()) {
                            checkbox.click();
                        }
                        selectedRows.put(name, true);
                        selectedCount++;
                        System.out.println("Selected checkbox for file: " + name);
                        break; // achou o arquivo e marcou, sai do loop
                    } catch (Exception e) {
                        System.out.println("Failed to select checkbox: " + e.getMessage());
                        fail("Failed to select checkbox for file: " + fileName);
                    }
                }
            }
        }

        if (selectedCount == 0) {
            System.out.println("File '" + fileName + "' not found or could not be selected.");
            return;
        }

        // Clicar no botão delete
        try {
            String buttonId = "edit-delete-selected-element";
            WebElement deleteButton = driver.findElement(By.id(buttonId));
            deleteButton.click();

            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            System.out.println("Delete confirmation alert: " + alert.getText());
            alert.accept();
        } catch (NoSuchElementException e) {
            fail("Delete button not found for type: " + type);
        } catch (NoAlertPresentException e) {
            fail("Expected confirmation alert not shown.");
        }

        // Esperar e verificar se o arquivo sumiu da tabela
        int attempts = 0;
        boolean stillExists = true;

        while (attempts < MAX_ATTEMPTS && stillExists) {
            Thread.sleep(WAIT_INTERVAL_MS);
            driver.navigate().refresh();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

            List<WebElement> updatedRows = driver.findElements(By.xpath("//table//tbody//tr"));
            stillExists = false;

            for (WebElement row : updatedRows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() >= 3) {
                    String name = cells.get(2).getText().trim();
                    if (name.equals(fileName)) {
                        stillExists = true;
                        break;
                    }
                }
            }

            System.out.println("Attempt " + (attempts + 1) + ": File still exists? " + stillExists);

            attempts++;
        }

        assertEquals(false, stillExists, "File '" + fileName + "' was not deleted.");
    }
    protected void deleteAllFiles(String type) throws InterruptedException {
        driver.get(FILES_URL + type + "/table/1/9/none");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        } catch (TimeoutException e) {
            fail("Table for type '" + type + "' not found.");
        }

        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        int selectedCount = 0;
        System.out.println("Total table rows found: " + rows.size());

        selectedRows.clear();

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 3) {
                String name = cells.get(2).getText().trim();

                try {
                    WebElement checkbox = cells.get(0).findElement(By.cssSelector("input[type='checkbox']"));
                    if (!checkbox.isSelected()) {
                        checkbox.click();
                    }
                    selectedRows.put(name, true);
                    selectedCount++;
                    System.out.println("Selected for deletion: " + name);
                } catch (Exception e) {
                    System.out.println("Failed to select checkbox for: " + name);
                }
            }
        }

        if (selectedCount == 0) {
            System.out.println("No files selected for deletion.");
            return;
        }

        try {
            String buttonId = "edit-delete-selected-element";
            WebElement deleteButton = driver.findElement(By.id(buttonId));
            deleteButton.click();

            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            System.out.println("Delete confirmation alert: " + alert.getText());
            alert.accept();
        } catch (NoSuchElementException e) {
            fail("Delete button not found for type: " + type);
        } catch (NoAlertPresentException e) {
            fail("Expected confirmation alert not shown.");
        }

        // Verificar se todos foram realmente apagados
        int attempts = 0;
        boolean someStillExist = true;

        while (attempts < MAX_ATTEMPTS && someStillExist) {
            Thread.sleep(WAIT_INTERVAL_MS);
            driver.navigate().refresh();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

            List<WebElement> updatedRows = driver.findElements(By.xpath("//table//tbody//tr"));
            someStillExist = false;

            for (WebElement row : updatedRows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() >= 3) {
                    String name = cells.get(2).getText().trim();
                    if (selectedRows.containsKey(name)) {
                        someStillExist = true;
                        break;
                    }
                }
            }

            System.out.println("Attempt " + (attempts + 1) + ": Some files still exist? " + someStillExist);
            attempts++;
        }

        assertEquals(false, someStillExist, "Some files were not deleted.");
    }


    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }
}

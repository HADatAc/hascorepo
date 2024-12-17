/* TEST REP MODULE BY SIMULATING AN INS TEMPLATE FILE ADD */


package tests_backup;

import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.BufferedReader;
import java.io.InputStreamReader;


@TestMethodOrder(OrderAnnotation.class)
public class REP extends BaseTest {

    private void executeShellCommand(String command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[OUTPUT] " + line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Failed to execute command: " + command + " (Exit code: " + exitCode + ")");
        }
    }



    @Test
    @Order(1)
    public void addNew() {
        try {
            authenticate();
            Thread.sleep(2000);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Actions actions = new Actions(driver);
            WebElement dropdownMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Manage Instrument Elements' and contains(text(), 'Instrument Element')]")));
            actions.moveToElement(dropdownMenu).click().perform();
            Thread.sleep(1000);

            WebElement subOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Manage INS Templates' and text()='Manage INS Templates']")));
            subOption.click();

            wait.until(ExpectedConditions.urlContains("rep/select/mt/ins/table/1/9/none"));
            WebElement addButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-add-element")));
            addButton.click();

            wait.until(ExpectedConditions.urlContains("/rep/manage/addmt/ins/none/F"));
            WebElement nameField = driver.findElement(By.id("edit-mt-name"));
            nameField.sendKeys(System.getProperty("ins-title"));
            WebElement versionField = driver.findElement(By.id("edit-mt-version"));
            versionField.sendKeys("1");

            String filePath = System.getProperty("user.dir") + System.getProperty("file");  // Usa o valor de -Dfile
            WebElement uploadField = driver.findElement(By.id("edit-mt-filename-upload"));
            uploadField.sendKeys(filePath);
            Thread.sleep(5000);

            WebElement saveButton = driver.findElement(By.id("edit-save-submit"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveButton);
            saveButton.click();

            wait.until(ExpectedConditions.urlContains("ep/select/mt/ins/table/1/9/none"));
            WebElement instrumentTable = driver.findElement(By.id("edit-element-table"));
            WebElement addedInstrument = instrumentTable.findElement(By.xpath("//td[contains(text(), '" + System.getProperty("ins-title") + "')]"));

            Assertions.assertNotNull(addedInstrument, "Instrument not found in the list");
        } catch (Exception e) {
            Assertions.fail("Add New Instrument failed: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void ingest() {
        try {
            authenticate();
            Thread.sleep(2000);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Actions actions = new Actions(driver);
            WebElement dropdownMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Manage Instrument Elements' and contains(text(), 'Instrument Element')]")));
            actions.moveToElement(dropdownMenu).click().perform();
            Thread.sleep(1000);

            WebElement subOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Manage INS Templates' and text()='Manage INS Templates']")));
            subOption.click();

            // CHANGE VIEW TO CARD
            wait.until(ExpectedConditions.urlContains("/rep/select/mt/ins/table/1/9/none"));
            WebElement addButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-card-view")));
            addButton.click();

            // CHECK IF THERE IS A CARD WITH TITLE 
            WebElement insCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[contains(text(), '" + System.getProperty("ins-title") + "')]/ancestor::div[contains(@class, 'card')]")));

            // CLICK "Ingest" BUTTON INSIDE CARD
            WebElement ingestButton = insCard.findElement(By.xpath(".//button[@data-drupal-selector='edit-ingest']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ingestButton);
            ingestButton.click();
            Thread.sleep(2000);

            // VERIFICATION LOOP INSIDE CARD
            boolean isProcessed = false;
            for (int i = 0; i < 10; i++) {
                // REFRESH PAGE
                driver.navigate().refresh();

                // LOCATE CARD AFTER REFRESH
                insCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[contains(text(), '" + System.getProperty("ins-title") + "')]/ancestor::div[contains(@class, 'card')]")));

                try {
                    // LOCATE ELEMENT INSIDE CARD
                    WebElement statusElement = insCard.findElement(By.xpath(".//*[@data-drupal-selector='edit-element-status']//font"));
                    String statusText = statusElement.getText().trim();

                    // VERIFY IS STATUS IS "PROCESSED"
                    if (statusText.equals("PROCESSED")) {
                        isProcessed = true;
                        break;
                    }
                } catch (NoSuchElementException e) {
                    isProcessed = false;
                }

                // WAITH UNTIL NEW RETRY
                Thread.sleep(7000);
            }

            // FINAL VERIFICATION TO ASSURE STATUS IS "PROCESSED"
            Assertions.assertTrue(isProcessed, "Status did not reach 'PROCESSED' after X attempts");

        } catch (Exception e) {
            Assertions.fail("Ingest INS Template failed: \n\n" + e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void exists() {
        try {
            authenticate();
            Thread.sleep(2000);
    
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Actions actions = new Actions(driver);
            WebElement dropdownMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Manage Instrument Elements' and contains(text(), 'Instrument Element')]")));
            actions.moveToElement(dropdownMenu).click().perform();
            Thread.sleep(1000);

            WebElement subOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Manage Instruments' and text()='Manage Instruments']")));
            subOption.click();

            // CHANGE VIEW TO CARD
            wait.until(ExpectedConditions.urlContains("/sir/select/instrument/1/9"));
            WebElement addButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-card-view")));
            addButton.click();
    
            // CHECK IF THERE IS A CARD WITH TITLE 
            wait.until(ExpectedConditions.urlContains("/sir/select/instrument/1/9"));
            WebElement sddCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[contains(text(), '" + System.getProperty("ins-card-title") + "')]/ancestor::div[contains(@class, 'card-header')]")));
    
            // FINAL VERIFICATION TO ASSURE CARD HAS BEEN FOUND
            Assertions.assertNotNull(sddCard, "Instrument not found");
    
        } catch (Exception e) {
            Assertions.fail("Instrument '" + System.getProperty("ins-card-title") + "' not found: \n\n" + e.getMessage());
        }
    }

    @Test
    @Order(4)
    public void backupAndRestoreValidation() {
        try {
            System.out.println("A fazer backup do sistema...");
            executeShellCommand("./backup.sh");

            System.out.println("A simular falha: apagar a base de dados...");
            executeShellCommand("docker exec drupal_db mysqladmin -u root -pLag00n --force drop drupal");

            System.out.println("A restaurar o sistema...");
            executeShellCommand("./restore.sh");

            System.out.println("À espera do sistema reiniciar...");
            Thread.sleep(2 * 60 * 1000);
            
            System.out.println("Validar o estado do sistema após restauração...");
            exists();

        } catch (Exception e) {
            Assertions.fail("Teste de backup e restauração falhou: " + e.getMessage());
        }
    }

}

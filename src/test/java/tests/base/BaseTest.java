package tests.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    public void setUp() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver"); // ajuste o caminho se necessário
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        login();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login() throws InterruptedException {
        driver.get("http://34.245.157.211/user/login");

        WebElement usernameInput = driver.findElement(By.id("edit-name"));
        WebElement passwordInput = driver.findElement(By.id("edit-pass"));
        WebElement loginButton = driver.findElement(By.id("edit-submit"));

        usernameInput.sendKeys("admin");  // ajuste se necessário
        passwordInput.sendKeys("admin");  // ajuste se necessário
        loginButton.click();

        Thread.sleep(2000); // espera o login completar
    }
}

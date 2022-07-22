package cookies;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HandlingCookiesTests {
    private ChromeDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    public void noConsentCookies() {
        driver.navigate().to("https://www.consentmanager.net/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cmpwrapper")));
        WebElement shadowRoot = (WebElement) driver.executeScript("return document.getElementById('cmpwrapper').shadowRoot.getElementById('cmpboxheadline1')");
       // var consentDivHeading =  consentDiv.getShadowRoot().findElement(By.id("cmpboxheadline1"));
        Assertions.assertEquals("We value your privacy!", shadowRoot.getText());
    }

    @Test
    public void setConsentCookies() {
        driver.navigate().to("https://www.consentmanager.net/");
        var consentDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cmpwrapper")));
        driver.manage().addCookie(new Cookie("__cmpcccu1", "aBPcfWCkgAwAzADUAIAC4FlgAA"));
        driver.manage().addCookie(new Cookie("__cmpconsent1", "BPcfWCkPcfWCkAfESDENDXAAAAAAAA"));
        driver.navigate().refresh();

        wait.until(ExpectedConditions.invisibilityOf(consentDiv));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

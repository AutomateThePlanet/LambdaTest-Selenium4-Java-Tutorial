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
    public void experimentWithCookies() {
        driver.navigate().to("https://www.consentmanager.net/");

        Cookie cname = new Cookie("userTracking", "12345678999");
        driver.manage().addCookie(cname);

        var cookiesList =  driver.manage().getCookies();
        for(Cookie cookie:cookiesList) {
            System.out.println(cookie);
        }

        //delete the newly created cookie
        driver.manage().deleteCookie(cname);
        var cookies =  driver.manage().getCookies();
        for(Cookie cookie :cookies) {
            System.out.println(cookie );
        }

        // When the sameSite attribute is set as Strict, the cookie will not be sent along with requests initiated by third party websites.
        // When you set a cookie sameSite attribute to Lax, the cookie will be sent along with the GET request initiated by third party website.
        Cookie csrfCookie = new Cookie.Builder("key", "value").sameSite("Lax").build();
        driver.manage().addCookie(csrfCookie);
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

    @Test
    public void simulateLogin() {
        driver.navigate().to("http://www.memotome.com/home.asp?display=reminder");

        driver.manage().addCookie(new Cookie("DisplayInfo1", "PADI=842919&DateFormat=%25A%2C+%25B+%25%23d%2C+%25Y&DisplayName=Testing+Selenium"));
        driver.manage().addCookie(new Cookie("UserInfo1", "UserId=45LCWNBH"));
        driver.navigate().to("http://www.memotome.com/home.asp?display=reminder");

        var testReminderAnchor = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(text(), 'TEST REMINDER')]")));
        Assertions.assertNotNull(testReminderAnchor);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

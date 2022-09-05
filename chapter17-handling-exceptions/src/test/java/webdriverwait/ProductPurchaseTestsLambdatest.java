package webdriverwait;

import com.google.common.base.Throwables;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v102.security.Security;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.*;

import static org.openqa.selenium.remote.http.Contents.utf8String;

public class ProductPurchaseTestsLambdatest {
    private final int WAIT_FOR_ELEMENT_TIMEOUT = 30;
    private WebDriver driver;
    private WebDriverWait webDriverWait;
    private Actions actions;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() throws MalformedURLException {
        String username = System.getenv("LT_USERNAME");
        String authkey = System.getenv("LT_ACCESSKEY");
        String hub = "@hub.lambdatest.com/wd/hub";

        var capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");
        HashMap<String, Object> ltOptions = new HashMap<String, Object>();
        ltOptions.put("user", username);
        ltOptions.put("accessKey", authkey);
        ltOptions.put("build", "Selenium 4");
        ltOptions.put("name",this.getClass().getName());
        ltOptions.put("platformName", "Windows 10");
        ltOptions.put("console", true);
        ltOptions.put("seCdp", true);
        ltOptions.put("selenium_version", "4.0.0");
        capabilities.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(new URL("https://" + username + ":" + authkey + hub), capabilities);
        Augmenter augmenter = new Augmenter();
        driver = augmenter.augment(driver);
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
        actions = new Actions(driver);
        driver.manage().window().maximize();
    }

    @Test
    @Order(1)
    public void completePurchaseSuccessfully_whenNewClient() throws InterruptedException {
        driver.navigate().to("http://demos.bellatrix.solutions/");
        var addToCartFalcon9 = findElement(By.cssSelector("[data-product_id*='28']"));
        addToCartFalcon9.click();
        var viewCartButton = findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();

        var couponCodeTextField = findElement(By.id("coupon_code"));
        couponCodeTextField.clear();
        couponCodeTextField.sendKeys("happybirthday");
        var applyCouponButton = findElement(By.cssSelector("[value*='Apply coupon']"));
        applyCouponButton.click();
        Thread.sleep(4000);
        var messageAlert = findElement(By.cssSelector("[class*='woocommerce-message']"));
        Assertions.assertEquals(messageAlert.getText(), "Coupon code applied successfully.");

        var quantityBox = findElement(By.cssSelector("[class*='input-text qty text']"));
        quantityBox.clear();
        quantityBox.sendKeys("2");

        var updateCart = findElement(By.cssSelector("[value*='Update cart']"));

        try {
            updateCart.click();
        } catch (ElementClickInterceptedException e) {
            ArrayList<String> exceptionCapture = new ArrayList<>();
            exceptionCapture.add(e.getMessage());
            var stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            exceptionCapture.add(stackTrace.toString());

            // via Guava
            String stackTraceText = Throwables.getStackTraceAsString(e);
            ((JavascriptExecutor) driver).executeScript("lambda-exceptions", exceptionCapture);

            ((JavascriptExecutor) driver).executeScript("lambda-status=failed");
        }


        Thread.sleep(4000);
        var totalSpan = findElement(By.xpath("//*[@class='order-total']//span"));
        Assertions.assertEquals("114.00€", totalSpan.getText());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private String getUserPasswordFromDb(String userName) {
        return "@purISQzt%%DYBnLCIhaoG6$";
    }

    private WebElement findElement(By by) {
        var webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    private List<WebElement> findElements(By by) {
        var webDriverWait = new WebDriverWait(driver,  Duration.ofSeconds(30));
        return webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }
}
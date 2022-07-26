package troubleshooting;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.events.DomMutationEvent;
import org.openqa.selenium.devtools.v103.emulation.Emulation;
import org.openqa.selenium.devtools.v103.log.Log;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.HasLogEvents;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.openqa.selenium.devtools.events.CdpEventTypes.domMutation;

public class DevToolsTroubleshootingLambdatestTests {
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
    public void verifyCalculatedDistance_when_countryGermany(){
        DevTools devTools = ((HasDevTools) driver).getDevTools();
        devTools.createSession();
        devTools.send(Emulation.setGeolocationOverride(Optional.of(52.520008),
                Optional.of(13.404954),
                Optional.of(1)));
        devTools.send(Emulation.setTimezoneOverride("Europe/Berlin"));
        devTools.send(Emulation.setLocaleOverride(Optional.of("de-DE")));

        // configure JS exceptions logging
        List<JavascriptException> jsExceptionsList = new ArrayList<>();
        Consumer<JavascriptException> addEntry = jsExceptionsList::add;
        devTools.getDomains().events().addJavascriptExceptionListener(addEntry);

        // configure console messages logging
        List<String> consoleMessages = new ArrayList<>();
        devTools.send(Log.enable());
        devTools.addListener(Log.entryAdded(),
                logEntry -> {
                    consoleMessages.add("log: " +logEntry.getText() + "level: " + logEntry.getLevel());
                });

        driver.navigate().to("https://www.gps-coordinates.net/");
        driver.manage().addCookie(new Cookie("cookieconsent_dismissed", "yes"));

        var map = waitAndFindElement(By.xpath("//*[@id='map_canvas']"));
        Assertions.assertTrue(map.isDisplayed());

        var addressTitle = waitAndFindElement(By.xpath("//*[@id='iwtitle']"));
        String address = addressTitle.getText();
        var addressParts = address.split(",");
        var lastPartAddress = addressParts[addressParts.length - 1];
        String currentAddress = String.format("%s, %s", lastPartAddress, "Berlin");

        driver.navigate().to("https://www.gps-coordinates.net/distance");

        var firstDistanceAddressInput = waitAndFindElement(By.id("address1"));
        firstDistanceAddressInput.sendKeys(currentAddress);

        var secondDistanceAddressInput = driver.findElement(By.id("address2"));
        secondDistanceAddressInput.sendKeys("Sofia, Bulgaria");

        var calculateDistanceButton = driver.findElement(By.xpath("//button[text()='Calculate the distance']"));
        actions.moveToElement(calculateDistanceButton).perform();

        ((JavascriptExecutor)driver).executeScript("console.log('jquery-migrate.min.js:2 JQMIGRATE: Migrate is installed, version 3.3.2')");
        ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
                calculateDistanceButton, "onclick", "throw new Error('Calculation error')");
        actions.moveToElement(calculateDistanceButton).click().perform();


        ArrayList<String> exceptionCapture = new ArrayList<>(jsExceptionsList.stream().map(s -> s.getMessage()).collect(Collectors.toList()));
        ((JavascriptExecutor)driver).executeScript("lambda-exceptions", exceptionCapture);


        // if contains specific message -> fail the test
//        if (consoleMessages.contains("jquery-migrate.min.js:2 JQMIGRATE: Migrate is installed, version 3.3.2")) {
//            ((JavascriptExecutor)driver).executeScript("lambda-status=failed");
//            Assertions.fail();
//        }
//
//        if (jsExceptionsList.stream().count() > 0) {
//            ((JavascriptExecutor)driver).executeScript("lambda-status=failed");
//            Assertions.fail();
//        }
    }

    private WebElement waitAndFindElement(By locator){
        var element= webDriverWait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollToVisible(element);
        return element;
    }

    public void scrollToVisible(WebElement element) {
        try {
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (ElementNotInteractableException ex) {
            // ignore
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
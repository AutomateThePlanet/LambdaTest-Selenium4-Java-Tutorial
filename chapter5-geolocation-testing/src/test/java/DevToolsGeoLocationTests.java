import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v103.emulation.Emulation;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;

public class DevToolsGeoLocationTests {
    private final int WAIT_FOR_ELEMENT_TIMEOUT = 30;
    private WebDriver driver;
    private WebDriverWait webDriverWait;
    private Actions actions;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        DevTools devTools = ((ChromeDriver)driver).getDevTools();
        devTools.createSession();
        devTools.send(Emulation.setGeolocationOverride(Optional.of(52.520008),
                Optional.of(13.404954),
                Optional.of(1)));

        devTools.send(Emulation.setTimezoneOverride("Europe/Berlin"));
        devTools.send(Emulation.setLocaleOverride(Optional.of("de-DE")));
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
        actions = new Actions(driver);
    }

    @Test
    public void verifyCalculatedDistance_when_countryGermany(){
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
        actions.moveToElement(calculateDistanceButton).click().perform();

        var distanceSpan = driver.findElement(By.id("distance"));

        webDriverWait.until(ExpectedConditions.textToBePresentInElement(distanceSpan, "1320.41 km / 820.47 mi"));
    }

    @ParameterizedTest(name = "{index}. verify distance from = {0} to {2}")
    @CsvSource(value = {
            "52.520008,13.404954,Berlin,0 km / 0 mi",
            "-58.381592,-34.603722,Buenos Aires,11951.47 km / 7426.3 mi",
            "149.128998,-35.282001,Canberra,15355.98 km / 9541.76 mi",
            "-75.695000,45.424721,Ottawa,7379.06 km / 4585.14 mi",
            "25.105497,121.597366,Taipei City,8789.28 km / 5461.41 mi",
            "59.911491,10.757933,Oslo,2098.7 km / 1304.07 mi",
            "33.918861,18.423300,Cape Town,8544.51 km / 5309.32 mi"
    })
    public void verifyDistance(String latitude, String longitude, String capital, String expectedDistance){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        DevTools devTools = ((ChromeDriver)driver).getDevTools();
        devTools.createSession();
        devTools.send(Emulation.setGeolocationOverride(Optional.of(Float.parseFloat(latitude)),
                Optional.of(Float.parseFloat(longitude)),
                Optional.of(1)));
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
        actions = new Actions(driver);

        driver.navigate().to("https://www.gps-coordinates.net/");
        driver.manage().addCookie(new Cookie("cookieconsent_dismissed", "yes"));
        var map = waitAndFindElement(By.xpath("//*[@id='map_canvas']"));
        Assertions.assertTrue(map.isDisplayed());

        var addressTitle = waitAndFindElement(By.xpath("//*[@id='iwtitle']"));
        String address = addressTitle.getText();
        var addressParts = address.split(",");
        var lastPartAddress = addressParts[addressParts.length - 1];
        String currentAddress = String.format("%s, %s", capital, lastPartAddress);

        driver.navigate().to("https://www.gps-coordinates.net/distance");

        var firstDistanceAddressInput = waitAndFindElement(By.id("address1"));
        firstDistanceAddressInput.sendKeys(currentAddress);

        var secondDistanceAddressInput = driver.findElement(By.id("address2"));
        secondDistanceAddressInput.sendKeys("Berlin, Germany");

        var calculateDistanceButton = driver.findElement(By.xpath("//button[text()='Calculate the distance']"));
        actions.moveToElement(calculateDistanceButton).click().perform();

        var distanceSpan = driver.findElement(By.id("distance"));

        webDriverWait.until(ExpectedConditions.textToBePresentInElement(distanceSpan, expectedDistance));
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
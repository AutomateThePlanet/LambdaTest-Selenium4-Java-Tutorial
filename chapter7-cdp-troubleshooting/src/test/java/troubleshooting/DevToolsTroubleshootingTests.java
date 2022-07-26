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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.openqa.selenium.devtools.events.CdpEventTypes.domMutation;

public class DevToolsTroubleshootingTests {
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

        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
        actions = new Actions(driver);
    }

    @Test
    public void verifyCalculatedDistance_when_countryGermany(){
        DevTools devTools = ((HasDevTools)driver).getDevTools();
        devTools.createSession();
        devTools.send(Emulation.setGeolocationOverride(Optional.of(52.520008),
                Optional.of(13.404954),
                Optional.of(1)));

        devTools.send(Emulation.setTimezoneOverride("Europe/Berlin"));
        devTools.send(Emulation.setLocaleOverride(Optional.of("de-DE")));

        List<DomMutationEvent> mutationsList = Collections.synchronizedList(new ArrayList<>());
        ((HasLogEvents)driver).onLogEvent(domMutation(mutation -> {
            mutationsList.add(mutation);
        }));

        // configure JS exceptions logging
        List<JavascriptException> jsExceptionsList = Collections.synchronizedList(new ArrayList<>());
        Consumer<JavascriptException> addEntry = jsExceptionsList::add;
        devTools.getDomains().events().addJavascriptExceptionListener(addEntry);

        // configure console messages logging
        List<String> consoleMessages = Collections.synchronizedList(new ArrayList<>());
        devTools.send(Log.enable());
        devTools.addListener(Log.entryAdded(),
                logEntry -> {
                    consoleMessages.add("log: " + logEntry.getText() + "level: " + logEntry.getLevel());
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

        ((JavascriptExecutor)driver).executeScript("console.log('jquery-migrate.min.js:2 JQMIGRATE: Migrate is installed, version 3.3.2')");
        ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
                calculateDistanceButton, "onclick", "throw new Error('Calculation error')");

        actions.moveToElement(calculateDistanceButton).click().perform();

        // display all mutations
        for (var mutation:mutationsList) {
            var attributeName = Optional.ofNullable(mutation.getAttributeName()).orElse("");
            var oldValue = Optional.ofNullable(mutation.getOldValue()).orElse("");
            var currentValue = Optional.ofNullable(mutation.getCurrentValue()).orElse("");
            var elementLocation = Optional.ofNullable(mutation.getElement().toString()).orElse("");
            System.out.println(String.format("attr name: %s\n old value = %s\n new value = %s\n element = %s\n\n", attributeName, oldValue, currentValue, elementLocation));
        }

        System.out.println("###########################");
        System.out.println();
        // print all JS errors
        for (JavascriptException jsException : jsExceptionsList) {
            System.out.println("JS exception message: " + jsException.getMessage());
            jsException.printStackTrace();
            System.out.println();
        }

        System.out.println("###########################");
        System.out.println();
        // print all console messages
        for (var consoleMessage : consoleMessages) {
            System.out.println(consoleMessage);
        }

        // if contains specific message -> fail the test
        if (consoleMessages.contains("jquery-migrate.min.js:2 JQMIGRATE: Migrate is installed, version 3.3.2")) {
            Assertions.fail();
        }

//        if (jsExceptionsList.stream().count() > 0) {
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
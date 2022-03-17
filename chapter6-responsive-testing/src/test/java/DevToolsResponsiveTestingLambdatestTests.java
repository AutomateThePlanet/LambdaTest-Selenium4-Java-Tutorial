import io.github.bonigarcia.wdm.WebDriverManager;
import layout.LayoutWebElement;
import layout.LayoutWebSelect;
import layout.SpecialComponents;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v95.emulation.Emulation;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;

public class DevToolsResponsiveTestingLambdatestTests {
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
        ltOptions.put("seCdp", true);
        ltOptions.put("selenium_version", "4.0.0");
        capabilities.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(new URL("https://" + username + ":" + authkey + hub), capabilities);
        Augmenter augmenter = new Augmenter();
        driver = augmenter.augment(driver);
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
        actions = new Actions(driver);
        driver.manage().window().maximize();

        DevTools devTools = ((ChromeDriver)driver).getDevTools();
        devTools.createSession();
        devTools.getDomains().network().setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 11.12; rv:68.0) Gecko/20100101 Firefox/67.0");

        devTools.send(Emulation.setDeviceMetricsOverride(1280,
                1024,
                50,
                false,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()));
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
        actions = new Actions(driver);
    }

    @Test
    public void testPageLayout(){
        driver.navigate().to("https://demos.bellatrix.solutions/");

        var sortDropDown = new LayoutWebSelect(new Select(driver.findElement(By.xpath("//*[contains(@class,'orderby')]"))));
        var protonRocketAnchor = new LayoutWebElement(driver.findElement(By.xpath("//*[contains(@href, '/proton-rocket/')]")));
        var protonMAnchor = new LayoutWebElement(driver.findElement(By.xpath("//*[contains(@href, '/proton-m/')]")));
        var saturnVAnchor = new LayoutWebElement(driver.findElement(By.xpath("//*[contains(@href, '/saturn-v/')]")));
        var falconHeavyAnchor = new LayoutWebElement(driver.findElement(By.xpath("//*[contains(@href, '/falcon-heavy/')]")));
        var falcon9Anchor = new LayoutWebElement(driver.findElement(By.xpath("//*[contains(@href, '/falcon-9/')]")));

        sortDropDown.above(protonRocketAnchor).validate();

        sortDropDown.above(protonRocketAnchor).equal(42).validate();

        sortDropDown.above(protonRocketAnchor).greaterThan(40).validate();
        sortDropDown.above(protonRocketAnchor).greaterThanOrEqual(42).validate();
        sortDropDown.above(protonRocketAnchor).lessThan(50).validate();
        sortDropDown.above(protonRocketAnchor).lessThanOrEqual(43).validate();

        sortDropDown.right(saturnVAnchor).validate();
        saturnVAnchor.left(sortDropDown).validate();

        protonRocketAnchor.alignedHorizontallyAll(protonMAnchor).validate();
        protonRocketAnchor.alignedHorizontallyTop(protonMAnchor, saturnVAnchor).validate();
        protonRocketAnchor.alignedHorizontallyCentered(protonMAnchor, saturnVAnchor).validate();
        protonRocketAnchor.alignedHorizontallyBottom(protonMAnchor, saturnVAnchor).validate();

        falcon9Anchor.alignedVerticallyAll(falconHeavyAnchor).validate();

        falcon9Anchor.alignedVerticallyLeft(falconHeavyAnchor).validate();
        falcon9Anchor.alignedVerticallyCentered(falconHeavyAnchor).validate();
        falcon9Anchor.alignedVerticallyRight(falconHeavyAnchor).validate();

        falcon9Anchor.height().lessThan(350).validate();
        falcon9Anchor.width().greaterThan(1).validate();
        falcon9Anchor.width().lessThan(345).validate();

        falcon9Anchor.inside(SpecialComponents.getViewport(driver));
        falcon9Anchor.inside(SpecialComponents.getScreen(driver));

    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
import io.github.bonigarcia.wdm.WebDriverManager;
import layout.LayoutWebElement;
import layout.LayoutWebSelect;
import layout.SpecialComponents;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v103.emulation.Emulation;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DevToolsResponsiveTests {
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
        // https://chromedriver.chromium.org/mobile-emulation
        Map<String, Object> deviceMetrics = new HashMap<>();
        deviceMetrics.put("width", 360);
        deviceMetrics.put("height", 640);
        deviceMetrics.put("pixelRatio", 3.0);
        Map<String, Object> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceMetrics", deviceMetrics);
        mobileEmulation.put("userAgent", "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
        // Map<String, String> mobileEmulation = new HashMap<>();
        // mobileEmulation.put("deviceName", "Nexus 5");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);

        driver = new ChromeDriver();
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

        sortDropDown.assertFontSize("14px");
        sortDropDown.assertFontWeight("400");
        sortDropDown.assertFontFamily("\"Source Sans Pro\", HelveticaNeue-Light, \"Helvetica Neue Light\", \"Helvetica Neue\", Helvetica, Arial, \"Lucida Grande\", sans-serif");

        protonRocketAnchor.assertColor("rgba(127, 84, 179, 1)");
        protonRocketAnchor.assertBackgroundColor("rgba(0, 0, 0, 0)");
        protonRocketAnchor.assertBorderColor("rgb(127, 84, 179)");

        protonRocketAnchor.assertTextAlign("center");
        protonRocketAnchor.assertVerticalAlign("baseline");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
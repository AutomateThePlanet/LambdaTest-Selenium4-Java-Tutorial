import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RelativeLocatorsTests {
    private WebDriver driver;
    private WebDriverWait webDriverWait;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    public void zipCodeTest() {
        driver.navigate().to("https://www.zip-codes.com/search.asp?selectTab=3");
        var cityInput = driver.findElement(By.xpath("//label[text()='Town/City:']/following-sibling::input"));

        var cityInput1 = driver.findElement(RelativeLocator.with(By.tagName("input")).toLeftOf(By.xpath("//label[text()='Town/City:']")));
        cityInput.sendKeys("Sofia");

        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[text()='Consent']")));
        var consentButton = driver.findElement(By.xpath("//p[text()='Consent']"));
        consentButton.click();

        var findZipCodesButton2 = driver.findElement(By.xpath("//h4[text()='Find ZIP Codes by City, State, Address, or Area Code']/following-sibling::form/input[@value='Find ZIP Codes']"));

        var findZipCodesButton1 = driver.findElement(RelativeLocator.with(By.xpath("//form/input[@value='Find ZIP Codes']")).below(By.xpath("//h4[text()='Find ZIP Codes by City, State, Address, or Area Code']")));

        var h4 = driver.findElement(By.xpath("//h4[text()='Find ZIP Codes by City, State, Address, or Area Code']"));
        var findZipCodesButton = driver.findElement(RelativeLocator.with(By.xpath("//form/input[@value='Find ZIP Codes']")).below(h4));

        findZipCodesButton2.click();

        webDriverWait.until(ExpectedConditions.elementToBeClickable(RelativeLocator.with(By.xpath("form/input[@value='Find ZIP Codes']")).below(By.xpath("//h4[text()='Find ZIP Codes by City, State, Address, or Area Code']"))));
        webDriverWait.until(ExpectedConditions.elementToBeClickable(RelativeLocator.with(By.xpath("//form/input[@value='Find ZIP Codes']")).below(h4)));
        findZipCodesButton.click();

        List<String> zipTableLinks = driver.findElements(By.xpath("//table[@class='statTable']/tbody/tr/td[1]/a")).
                stream().limit(10).map(a -> String.format("https://www.zip-codes.com/%s", a.getAttribute("href"))).collect(Collectors.toList());

        List<ZipInfo> zipInfos = new ArrayList<>();
        zipTableLinks.stream().forEach(l -> {
            driver.navigate().to(l);
            zipInfos.add(new ZipInfo(
                    findZipColumnDataByLabel("City"),
                    findZipColumnDataByLabel("State"),
                    findZipColumnDataByLabel("Zip Code"),
                    findZipColumnDataByLabel("Longitude"),
                    findZipColumnDataByLabel("Latitude")));
        });
    }

    private String findZipColumnDataByLabel(String label) {
        // return driver.findElement(RelativeLocator.with(By.tagName("td")).below(By.tagName("td")).above(By.xpath(String.format("//span[text() = '%s:']", label)))).getText();
        //return driver.findElement(By.xpath(String.format("//span[text() = '%s:']/parent::td/following-sibling::td", label))).getText();
        return driver.findElement(RelativeLocator.with(By.tagName("td")).toRightOf(By.xpath(String.format("//span[text() = '%s:']", label)))).getText();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
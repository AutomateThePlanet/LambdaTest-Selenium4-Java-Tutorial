import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.events.DomMutationEvent;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class InitializationScriptTests {
    private WebDriver driver;
    private WebDriverWait webDriverWait;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    public void sendNotificationsMessages() throws InterruptedException {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        DevTools devTools = ((ChromeDriver)driver).getDevTools();
        devTools.createSession();
        devTools.getDomains().javascript().pin("notifications","""
        window.onload = () => {
            if (!window.jQuery) {
                var jquery = document.createElement('script'); 
                jquery.type = 'text/javascript';
                jquery.src = 'https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js';
                document.getElementsByTagName('head')[0].appendChild(jquery);
            } else {
                $ = window.jQuery;
            }

            $.getScript('https://cdnjs.cloudflare.com/ajax/libs/jquery-jgrowl/1.4.8/jquery.jgrowl.min.js')
            $('head').append('<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-jgrowl/1.4.8/jquery.jgrowl.min.css" type="text/css" />');
        }
        """);
       // devTools.getDomains().javascript().addJsBinding("notifications");
        var listener = new WebDriverListener() {
            @Override
            public void beforeAnyWebElementCall(WebElement element, Method method, Object[] args) {
                try {
                    growlMessage(String.format("About to call a method %s in element", method.getName()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterAnyWebElementCall(WebElement element, Method method, Object[] args, Object result) {
                try {
                    growlMessage(String.format("%s called for element %s", method.getName(), element.getTagName()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeAnyWebDriverCall(WebDriver driver, Method method, Object[] args) {
                try {
                    growlMessage(String.format("%s webdriver call", method.getName()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterAnyWebDriverCall(WebDriver driver, Method method, Object[] args, Object result) {
                try {
                    growlMessage(String.format("%s webdriver call", method.getName()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        var decoratedDriver = new EventFiringDecorator(listener).decorate(driver);
        webDriverWait = new WebDriverWait(decoratedDriver, Duration.ofSeconds(30));

        decoratedDriver.get("https://www.zip-codes.com/search.asp?selectTab=3");


        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[text()='Consent']")));
        var consentButton = decoratedDriver.findElement(By.xpath("//p[text()='Consent']"));
        consentButton.click();

        var cityInput = decoratedDriver.findElement(By.xpath("//label[text()='Town/City:']/following-sibling::input"));
        cityInput.sendKeys("ANT");
        var h4 = decoratedDriver.findElement(By.xpath("//h4[text()='Find ZIP Codes by City, State, Address, or Area Code']"));
        var findZipCodesButton = decoratedDriver.findElement(RelativeLocator.with(By.xpath("//form/input[@value='Find ZIP Codes']")).below(h4));
        ///findZipCodesButton.click();

        //webDriverWait.until(ExpectedConditions.elementToBeClickable(RelativeLocator.with(By.xpath("//form/input[@value='Find ZIP Codes']")).below(By.xpath("//h4[text()='Find ZIP Codes by City, State, Address, or Area Code']"))));
        webDriverWait.until(ExpectedConditions.elementToBeClickable(RelativeLocator.with(By.xpath("//form/input[@value='Find ZIP Codes']")).below(h4)));
        findZipCodesButton.click();

        List<String> zipTableLinks = decoratedDriver.findElements(By.xpath("//table[@class='statTable']/tbody/tr/td[1]/a")).
                stream().limit(10).map(a -> String.format("https://www.zip-codes.com/%s", a.getAttribute("href"))).collect(Collectors.toList());

        List<ZipInfo> zipInfos = new ArrayList<>();
        zipTableLinks.stream().forEach(l -> {
            decoratedDriver.navigate().to(l);
            try {
                growlMessage("navigate to " + l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            zipInfos.add(new ZipInfo(
                    findZipColumnDataByLabel("City"),
                    findZipColumnDataByLabel("State"),
                    findZipColumnDataByLabel("Zip Code"),
                    findZipColumnDataByLabel("Longitude"),
                    findZipColumnDataByLabel("Latitude")));
        });
    }

    private  void growlMessage(String message) throws InterruptedException {
        Thread.sleep(1000);
        ((JavascriptExecutor)driver).executeScript(String.format("$.jGrowl('%s', { header: 'Important' });", message));
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
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

public class InitializationScriptTests {
    private final int WAIT_FOR_ELEMENT_TIMEOUT = 30;
    private WebDriver driver;
    private WebDriver decoratedDriver;
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
        
        function highlight(element){
            let defaultBG = element.style.backgroundColor;
            let defaultOutline = element.style.outline;
            element.style.backgroundColor = '#FDFF47';
            element.style.outline = '#f00 solid 2px';
        
            setTimeout(function()
            {
                element.style.backgroundColor = defaultBG;
                element.style.outline = defaultOutline;
            }, 1000);
        }
        """);

        var listener = new WebDriverListener() {
            @Override
            public void beforeAnyWebElementCall(WebElement element, Method method, Object[] args) {
                try {
                    growlMessage(String.format("About to call a method %s in element", method.getName()));
                    //highlightElement(element);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterAnyWebElementCall(WebElement element, Method method, Object[] args, Object result) {
                try {
                    highlightElement(element);
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

        decoratedDriver = new EventFiringDecorator(listener).decorate(driver);
        webDriverWait = new WebDriverWait(decoratedDriver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
        actions = new Actions(decoratedDriver);
    }

    @Test
    public void verifyToDoListCreatedSuccessfully_noParams(){
        decoratedDriver.navigate().to("https://todomvc.com/");
        openTechnologyApp("Backbone.js");
        addNewToDoItem("Clean the car");
        addNewToDoItem("Clean the house");
        addNewToDoItem("Buy Ketchup");
        getItemCheckbox("Buy Ketchup").click();

        assertLeftItems(2);
    }

    private void assertLeftItems(int expectedCount){
        var resultSpan = waitAndFindElement(By.xpath("//footer/*/span | //footer/span"));
        if (expectedCount == 1){
            var expectedText = String.format("%d item left", expectedCount);
            validateInnerTextIs(resultSpan, expectedText);
        } else {
            var expectedText = String.format("%d items left", expectedCount);
            validateInnerTextIs(resultSpan, expectedText);
        }
    }

    private void validateInnerTextIs(WebElement resultElement, String expectedText){
        webDriverWait.until(ExpectedConditions.textToBePresentInElement(resultElement, expectedText));
    }

    private WebElement getItemCheckbox(String todoItem){
        var xpathLocator = String.format("//label[text()='%s']/preceding-sibling::input", todoItem);
        return waitAndFindElement(By.xpath(xpathLocator));
    }

    private void openTechnologyApp(String technologyName){
        var technologyLink = waitAndFindElement(By.linkText(technologyName));
        technologyLink.click();
    }

    private void addNewToDoItem(String todoItem){
        var todoInput = waitAndFindElement(By.xpath("//input[@placeholder='What needs to be done?']"));
        todoInput.sendKeys(todoItem);
        actions.click(todoInput).sendKeys(Keys.ENTER).perform();
    }

    private WebElement waitAndFindElement(By locator){
        return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private Stream<Arguments> provideWebTechnologiesMultipleParams() {
        return Stream.of(
                Arguments.of("AngularJS", List.of("Buy Ketchup", "Buy House", "Buy Paper", "Buy Milk", "Buy Batteries"), List.of("Buy Ketchup", "Buy House"), 3),
                Arguments.of("React", List.of("Buy Ketchup", "Buy House", "Buy Paper", "Buy Milk", "Buy Batteries"), List.of("Buy Paper", "Buy Milk", "Buy Batteries"), 2),
                Arguments.of("Vue.js", List.of("Buy Ketchup", "Buy House", "Buy Paper", "Buy Milk", "Buy Batteries"), List.of("Buy Paper", "Buy Milk", "Buy Batteries"), 2),
                Arguments.of("Angular 2.0", List.of("Buy Ketchup", "Buy House", "Buy Paper", "Buy Milk", "Buy Batteries"), List.of(), 5)
        );
    }

    private void highlightElement(WebElement element) {
        ((JavascriptExecutor)driver).executeScript("highlight(arguments[0])", element);
    }

    private void growlMessage(String message) throws InterruptedException {
//        Thread.sleep(500);
        ((JavascriptExecutor)driver).executeScript(String.format("$.jGrowl('%s', { header: 'Important' });", message));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
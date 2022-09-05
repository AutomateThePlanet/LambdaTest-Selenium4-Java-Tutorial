package decoratorpattern;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WebCoreDriver extends Driver {
    private WebDriver webDriver;
    private WebDriverWait webDriverWait;

    @Override
    public void start(Browser browser) {
        switch (browser) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                webDriver = new ChromeDriver();
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                webDriver = new FirefoxDriver();
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
                webDriver = new EdgeDriver();
                break;
            case SAFARI:
                webDriver = new SafariDriver();
                break;
            case INTERNET_EXPLORER:
                WebDriverManager.iedriver().setup();
                webDriver = new InternetExplorerDriver();
                break;
            default:
                throw new IllegalArgumentException(browser.name());
        }

        // JAVA version 12:
//        webDriver = switch (browser) {
//            case CHROME -> {
//                WebDriverManager.chromedriver().setup();
//                yield new ChromeDriver();
//            }
//            case FIREFOX -> {
//                WebDriverManager.firefoxdriver().setup();
//                yield new FirefoxDriver();
//            }
//            case EDGE -> {
//                WebDriverManager.edgedriver().setup();
//                yield new EdgeDriver();
//            }
//            case OPERA -> {
//                WebDriverManager.operadriver().setup();
//                yield new OperaDriver();
//            }
//            case SAFARI -> new SafariDriver();
//            case INTERNET_EXPLORER -> {
//                WebDriverManager.iedriver().setup();
//                yield  new InternetExplorerDriver();
//            }
//            default-> {
//                WebDriverManager.chromedriver().setup();
//                yield new ChromeDriver();
//            }
//        };

        webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
    }

    @Override
    public void quit() {
        webDriver.quit();
    }

    @Override
    public void goToUrl(String url) {
        webDriver.navigate().to(url);
    }

    @Override
    public Element findElement(By locator) {
        var nativeWebElement =
                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(locator));
        Element element = new WebCoreElement(webDriver, nativeWebElement, locator);

        // If we use log decorator.
        Element logElement = new LogElement(element);

        return logElement;
    }

    @Override
    public List<Element> findElements(By locator) {
        List<WebElement> nativeWebElements =
                webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        var elements = new ArrayList<Element>();
        for (WebElement nativeWebElement:nativeWebElements) {
            Element element = new WebCoreElement(webDriver, nativeWebElement, locator);
            Element logElement = new LogElement(element);
            elements.add(logElement);
        }

        return elements;
    }
}

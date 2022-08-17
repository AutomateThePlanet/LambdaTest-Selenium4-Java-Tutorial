package adapterpattern;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DriverAdapter extends Driver {
    private WebDriver webDriver;
    private WebDriverWait webDriverWait;

    @Override
    public void start(Browser browser) {
        switch (browser) {
            case CHROME -> {
                WebDriverManager.chromedriver().setup();
                webDriver = new ChromeDriver();
            }
            case FIREFOX -> {
                WebDriverManager.firefoxdriver().setup();
                webDriver = new FirefoxDriver();
            }
            case EDGE -> {
                WebDriverManager.edgedriver().setup();
                webDriver = new EdgeDriver();
            }
            case OPERA -> {
                WebDriverManager.operadriver().setup();
                webDriver = new OperaDriver();
            }
            case SAFARI -> webDriver = new SafariDriver();
            case INTERNET_EXPLORER -> {
                WebDriverManager.iedriver().setup();
                webDriver = new InternetExplorerDriver();
            }
            default -> throw new IllegalArgumentException(browser.name());
        }

        webDriverWait = new WebDriverWait(webDriver,  Duration.ofSeconds(30));
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
        Element element = new ElementAdapter(webDriver, nativeWebElement, locator);

        return element;
    }

    @Override
    public List<Element> findElements(By locator) {
        List<WebElement> nativeWebElements =
                webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        var elements = new ArrayList<Element>();
        for (WebElement nativeWebElement:nativeWebElements) {
            Element element = new ElementAdapter(webDriver, nativeWebElement, locator);
            elements.add(element);
        }

        return elements;
    }

    @Override
    public void waitForAjax() {
        var js = (JavascriptExecutor)webDriver;
        webDriverWait.until(wd -> js.executeScript("return jQuery.active").toString() == "0");
    }
}

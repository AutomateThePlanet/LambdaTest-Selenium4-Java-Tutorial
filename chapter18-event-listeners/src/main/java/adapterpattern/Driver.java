package adapterpattern;

import org.openqa.selenium.By;
import org.openqa.selenium.support.events.WebDriverListener;

import java.util.List;

public abstract class Driver {
    public abstract void start(Browser browser);
    public abstract void quit();
    public abstract void goToUrl(String url);
    public abstract void waitForAjax();
    public abstract List<WebDriverListener> getWebDriverListener();
    public abstract Element findElement(By locator);
    public abstract List<Element> findElements(By locator);
}
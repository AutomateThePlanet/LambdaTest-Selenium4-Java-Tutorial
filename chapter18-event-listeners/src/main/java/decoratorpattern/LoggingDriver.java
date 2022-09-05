package decoratorpattern;

import org.openqa.selenium.By;

import java.util.List;

public class LoggingDriver extends DriverDecorator {
    public LoggingDriver(Driver driver) {
        super(driver);
    }

    @Override
    public void start(Browser browser) {
        System.out.print(String.format("start browser = %s", browser.name()));
        driver.start(browser);
    }

    @Override
    public void quit() {
        System.out.print("close browser");
        driver.quit();
    }

    @Override
    public void goToUrl(String url) {
        System.out.print(String.format("go to url = %s", url));
        driver.goToUrl(url);
    }

    @Override
    public Element findElement(By locator) {
        System.out.print("find element");
        return driver.findElement(locator);
    }

    @Override
    public List<Element> findElements(By locator) {
        System.out.print("find elements");
        return driver.findElements(locator);
    }
}

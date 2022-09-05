package listeners;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

public class HighlightElementWebDriverListener implements WebDriverListener {
    public void afterFindBy(By arg0, WebElement arg1, WebDriver arg2) {
       highlightElement(arg2, arg1);
    }

    private void highlightElement(WebDriver driver, WebElement element) {
        ((JavascriptExecutor)driver).executeScript("highlight(arguments[0])", element);
    }
}

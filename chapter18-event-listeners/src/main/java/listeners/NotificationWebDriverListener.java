package listeners;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

public class NotificationWebDriverListener implements WebDriverListener {
    public void afterClickOn(WebElement arg0, WebDriver arg1) throws InterruptedException {
        growlMessage(arg1, "inside method afterClickOn on " + arg0.toString());
    }

    public void afterFindBy(By arg0, WebElement arg1, WebDriver arg2) throws InterruptedException {
        growlMessage(arg2, "Find happened on " + arg1.toString() + " Using method " + arg0.toString());
    }

    public void afterNavigateTo(String arg0, WebDriver arg1) throws InterruptedException {
        growlMessage(arg1, "Inside the afterNavigateTo to " + arg0);
    }

    public void onException(Throwable arg0, WebDriver arg1) throws InterruptedException {
        growlMessage(arg1, "Exception occurred at " + arg0.getMessage());
    }

    private void growlMessage(WebDriver driver, String message) throws InterruptedException {
//        Thread.sleep(500);
        ((JavascriptExecutor)driver).executeScript(String.format("$.jGrowl('%s', { header: 'Important' });", message));
    }
}

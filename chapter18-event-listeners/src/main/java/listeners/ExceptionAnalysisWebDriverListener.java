package listeners;

import com.google.common.base.Throwables;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class ExceptionAnalysisWebDriverListener implements WebDriverListener {
    public void onException(Throwable e, WebDriver driver) {
        ArrayList<String> exceptionCapture = new ArrayList<>();
        exceptionCapture.add(e.getMessage());
        String stackTraceText = Throwables.getStackTraceAsString(e);
        exceptionCapture.add(stackTraceText);

        ((JavascriptExecutor) driver).executeScript("lambda-exceptions", exceptionCapture);

        ((JavascriptExecutor) driver).executeScript("lambda-status=failed");
    }
}

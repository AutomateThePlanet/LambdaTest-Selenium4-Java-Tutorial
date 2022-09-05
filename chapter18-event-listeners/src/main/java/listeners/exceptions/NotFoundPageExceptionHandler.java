package listeners.exceptions;

import org.openqa.selenium.WebDriver;

public class NotFoundPageExceptionHandler extends HtmlSourceExceptionHandler {

    public NotFoundPageExceptionHandler() {
        super("404 not found");
    }

    @Override
    public String getDetailedIssueExplanation() {
        return "the web page was not found";
    }
}
package listeners.exceptions;

import org.openqa.selenium.WebDriver;

public abstract class UrlExceptionHandler implements ExceptionAnalysationHandler {
    private String textToSearchInUrl;
    private String actualUrl;

    public UrlExceptionHandler(String textToSearchInUrl) {
        this.textToSearchInUrl = textToSearchInUrl;
    }

    public String getTextToSearchInUrl() {
        return textToSearchInUrl;
    }

    public String getActualUrl() {
        return actualUrl;
    }

    public abstract String getDetailedIssueExplanation();

    @Override
    public Boolean isApplicable(WebDriver driver, Exception ex, Object... context) {
        actualUrl = driver.getCurrentUrl();
        return driver.getCurrentUrl().contains(textToSearchInUrl);
    }
}

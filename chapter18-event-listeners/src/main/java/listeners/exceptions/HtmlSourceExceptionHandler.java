package listeners.exceptions;

import org.openqa.selenium.WebDriver;

public abstract class HtmlSourceExceptionHandler implements ExceptionAnalysationHandler {
    private String textToSearchInSource;
    private String pageSource;

    public String getTextToSearchInSource() {
        return textToSearchInSource;
    }

    public String getPageSource() {
        return pageSource;
    }

    public HtmlSourceExceptionHandler(String textToSearchInSource) {
        this.textToSearchInSource = textToSearchInSource;
    }

    @Override
    public Boolean isApplicable(WebDriver driver, Exception ex, Object... context) {
        pageSource = driver.getPageSource();
        return driver.getPageSource().contains(textToSearchInSource);
    }
}

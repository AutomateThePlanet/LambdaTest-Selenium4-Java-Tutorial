package listeners.exceptions;

import org.openqa.selenium.WebDriver;

public interface ExceptionAnalysationHandler {
    String getDetailedIssueExplanation();
    Boolean isApplicable(WebDriver driver, Exception ex, Object... context);
}

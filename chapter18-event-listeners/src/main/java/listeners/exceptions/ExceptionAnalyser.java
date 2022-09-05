package listeners.exceptions;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class ExceptionAnalyser {
    private static final List<ExceptionAnalysationHandler> exceptionAnalysationHandlers = new ArrayList<>();

    public static void removeFirstExceptionAnalysationHandler() {
        if (exceptionAnalysationHandlers.stream().count() > 0)
        {
            exceptionAnalysationHandlers.remove(0);
        }
    }

    public static void analyse(WebDriver driver, Exception ex, Object... context) throws AnalyzedTestException {
        for (ExceptionAnalysationHandler exceptionHandler : exceptionAnalysationHandlers) {
            if (exceptionHandler.isApplicable(driver, ex, context)) {
                if (driver != null)
                {
                    String url = driver.getCurrentUrl();
                    throw new AnalyzedTestException(exceptionHandler.getDetailedIssueExplanation(), url, ex);
                }
                else
                {
                    throw new AnalyzedTestException(exceptionHandler.getDetailedIssueExplanation(), ex);
                }
            }
        }
    }

    public static <TExceptionAnalysationHandler extends ExceptionAnalysationHandler> void addExceptionAnalysationHandler(ExceptionAnalysationHandler exceptionAnalysationHandler) {
        exceptionAnalysationHandlers.add(0, exceptionAnalysationHandler);
    }
}

package listeners.exceptions;

public class ErrorPageExceptionHandler extends HtmlSourceExceptionHandler {
    public ErrorPageExceptionHandler() {
        super("internal server error");
    }

    @Override
    public String getDetailedIssueExplanation() {
        return "page failed via internal server error\nPage Source:\n" + getPageSource();
    }
}
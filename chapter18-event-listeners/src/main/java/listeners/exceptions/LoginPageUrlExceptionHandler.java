package listeners.exceptions;

public class LoginPageUrlExceptionHandler extends UrlExceptionHandler {

    public LoginPageUrlExceptionHandler() {
        super("login");
    }

    @Override
    public String getDetailedIssueExplanation() {
        return "the test failed on the login web page so probably there is a problem with the authentication." + getActualUrl();
    }
}

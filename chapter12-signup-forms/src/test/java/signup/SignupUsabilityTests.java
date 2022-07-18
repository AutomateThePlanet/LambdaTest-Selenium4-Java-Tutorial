package signup;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v95.log.Log;
import pages.AccountSuccessPage;
import pages.RegistrationPage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SignupUsabilityTests {
    private WebDriver driver;
    private List<JavascriptException> jsExceptionsList;
    private List<String> consoleMessages;
    private RegistrationPage registrationPage;
    private AccountSuccessPage accountSuccessPage;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        registrationPage = new RegistrationPage(driver);
        accountSuccessPage = new AccountSuccessPage(driver);

        DevTools devTools = ((HasDevTools)driver).getDevTools();
        devTools.createSession();

        // configure JS exceptions logging
        jsExceptionsList = new ArrayList<>();
        Consumer<JavascriptException> addEntry = jsExceptionsList::add;
        devTools.getDomains().events().addJavascriptExceptionListener(addEntry);

        // configure console messages logging
        List<String> consoleMessages = new ArrayList<>();
        devTools.send(Log.enable());
        devTools.addListener(Log.entryAdded(),
                logEntry -> {
                    consoleMessages.add("log: " + logEntry.getText() + "level: " + logEntry.getLevel());
                });
    }

    // links
    @Test
    public void correctLoginLinkSet_when_navigateToRegistrationPage() {
        registrationPage.open();

        Assertions.assertEquals("https://ecommerce-playground.lambdatest.io/index.php?route=account/login", registrationPage.loginPageLink().getAttribute("href"));
    }

    @Test
    public void correctPrivacyPolicyLinkSet_when_navigateToRegistrationPage() {
        registrationPage.open();

        Assertions.assertEquals("https://ecommerce-playground.lambdatest.io/index.php?route=information/information/agree&information_id=3", registrationPage.privacyPolicyLink().getAttribute("href"));
    }

    // validate labels
    @Test
    public void registerAccountTitleDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        Assertions.assertEquals("Register Account", registrationPage.mainHeading().getText());
    }

    @Test
    public void firstNameLabelDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        Assertions.assertTrue(registrationPage.fistNameLabel().isDisplayed());
    }

    @Test
    public void lastNameLabelDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        Assertions.assertTrue(registrationPage.lastNameLabel().isDisplayed());
    }

    @Test
    public void emailLabelDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        Assertions.assertTrue(registrationPage.emailLabel().isDisplayed());
    }

    @Test
    public void telephoneLabelDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        Assertions.assertTrue(registrationPage.telephoneLabel().isDisplayed());
    }

    @Test
    public void passwordLabelDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        Assertions.assertTrue(registrationPage.passwordLabel().isDisplayed());
    }

    @Test
    public void confirmPasswordLabelDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        Assertions.assertTrue(registrationPage.passwordConfirmLabel().isDisplayed());
    }

    // check placeholders

    @Test
    public void correctFirstNamePlaceholderDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        registrationPage.assertPlaceholder("First Name", registrationPage.fistNameInput());
    }

    @Test
    public void correctLastNamePlaceholderDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        registrationPage.assertPlaceholder("Last Name", registrationPage.lastNameInput());
    }

    @Test
    public void correctEmailPlaceholderDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        registrationPage.assertPlaceholder("E-Mail", registrationPage.emailInput());
    }

    @Test
    public void correctTelephonePlaceholderDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        registrationPage.assertPlaceholder("Telephone", registrationPage.telephoneInput());
    }

    @Test
    public void correctPasswordPlaceholderDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        registrationPage.assertPlaceholder("Password", registrationPage.telephoneInput());
    }

    @Test
    public void correctConfirmPasswordPlaceholderDisplayed_when_navigateToRegistrationPage() {
        registrationPage.open();

        registrationPage.assertPlaceholder("Password Confirm", registrationPage.telephoneInput());
    }

    @Test
    public void continueButtonNameCorrectly_when_navigateToRegistrationPage() {
        registrationPage.open();

        Assertions.assertEquals("Continue", registrationPage.continueButton().getText());
    }

    @AfterEach
    public void tearDown() {
        System.out.println("###########################");
        System.out.println();
        // print all JS errors
        for (JavascriptException jsException : jsExceptionsList) {
            System.out.println("JS exception message: " + jsException.getMessage());
            jsException.printStackTrace();
            System.out.println();
        }

        System.out.println("###########################");
        System.out.println();
        // print all console messages
        for (var consoleMessage : consoleMessages) {
            System.out.println(consoleMessage);
        }

        // if contains specific message -> fail the test
        if (consoleMessages.contains("jquery-migrate.min.js:2 JQMIGRATE: Migrate is installed, version 3.3.2")) {
            Assertions.fail();
        }

        if (driver != null) {
            driver.quit();
        }
    }
}

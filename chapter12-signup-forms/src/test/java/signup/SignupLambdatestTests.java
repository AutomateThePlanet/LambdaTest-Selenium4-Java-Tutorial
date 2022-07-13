package signup;

import factories.UserFactory;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v95.emulation.Emulation;
import org.openqa.selenium.devtools.v95.log.Log;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.AccountSuccessPage;
import pages.RegistrationPage;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SignupLambdatestTests {
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
    public void setUp() throws MalformedURLException {
        String username = System.getenv("LT_USERNAME");
        String authkey = System.getenv("LT_ACCESSKEY");
        String hub = "@hub.lambdatest.com/wd/hub";

        var capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");
        HashMap<String, Object> ltOptions = new HashMap<String, Object>();
        ltOptions.put("user", username);
        ltOptions.put("accessKey", authkey);
        ltOptions.put("build", "Selenium 4");
        ltOptions.put("name",this.getClass().getName());
        ltOptions.put("platformName", "Windows 10");
        ltOptions.put("console", true);
        ltOptions.put("seCdp", true);
        ltOptions.put("selenium_version", "4.0.0");
        capabilities.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(new URL("https://" + username + ":" + authkey + hub), capabilities);
        Augmenter augmenter = new Augmenter();
        driver = augmenter.augment(driver);
        driver.manage().window().maximize();

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

    // happy path
    @Test
    public void userCreatedSuccessfully_when_allRequiredFieldsField_and_clickContinueButton() {
        var user = UserFactory.createDefault();

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_allRequiredFieldsField_and_pressContinueButtonWithEnter() {
        var user = UserFactory.createDefault();

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    // boundary values

    @Test
    public void userCreatedSuccessfully_when_firstName1Character() {
        var user = UserFactory.createDefault();
        user.setFirstName(StringUtils.repeat("A", 1));

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_firstName32Characters() {
        var user = UserFactory.createDefault();
        user.setFirstName(StringUtils.repeat("A", 32));

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_lastName1Character() {
        var user = UserFactory.createDefault();
        user.setLastName(StringUtils.repeat("A", 1));

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_lastName32Characters() {
        var user = UserFactory.createDefault();
        user.setLastName(StringUtils.repeat("A", 32));

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_email4Character() {
        var user = UserFactory.createDefault();
        user.setEmail("a@a.a");

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_email32Characters() {
        var user = UserFactory.createDefault();
        user.setEmail("a@" + StringUtils.repeat("A", 26) + ".com");

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_telephone3Character() {
        var user = UserFactory.createDefault();
        user.setTelephone("123");

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @ParameterizedTest(name = "{index}. user created successfully when correct telephone set for country = {0}")
    @ValueSource(strings = {
            "+9370123456789",
            "+358457012345678",
            "+3584570123456789",
            "+35845701234567890",
            "+35567123456789",
            "+2135123456789",
            "+97335512345678",
    })
    public void userCreatedSuccessfully_when_correctTelephoneSetForCountry(String telephone) {
        var user = UserFactory.createDefault();
        user.setTelephone("telephone");

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_telephone32Characters() {
        var user = UserFactory.createDefault();
        user.setTelephone(StringUtils.repeat("9", 33));

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_password4Character() {
        var user = UserFactory.createDefault();
        user.setPassword("1234");
        user.setPasswordConfirm("1234");

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_password20Characters() {
        var user = UserFactory.createDefault();
        user.setPassword(StringUtils.repeat("9", 20));
        user.setPasswordConfirm(StringUtils.repeat("9", 20));

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
    }

    @Test
    public void userCreatedSuccessfully_when_newsletterSubscribeTrue() {
        var user = UserFactory.createDefault();
        user.setShouldSubscribe(true);

        registrationPage.open();
        registrationPage.register(user, false);

        accountSuccessPage.assertAccountCreatedSuccessfully();
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

        if (driver != null) {
            driver.quit();
        }
    }
}
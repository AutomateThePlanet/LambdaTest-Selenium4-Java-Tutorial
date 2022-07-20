package signup;

import com.mailslurp.apis.InboxControllerApi;
import com.mailslurp.apis.WaitForControllerApi;
import com.mailslurp.clients.ApiClient;
import com.mailslurp.clients.ApiException;
import com.mailslurp.clients.Configuration;
import com.mailslurp.models.Email;
import com.mailslurp.models.InboxDto;
import com.mailslurp.models.SendEmailOptions;
import factories.UserFactory;
import infrastructure.MailslurpService;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v95.log.Log;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.AccountSuccessPage;
import pages.RegistrationPage;
import utilities.ResourcesReader;
import utilities.TimestampBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class EmailInboxesLambdatestTests {
    private WebDriver driver;
    private static ApiClient defaultClient = Configuration.getDefaultApiClient();
    private static InboxControllerApi  inboxControllerApi;
    private String API_KEY = "52b1b438245522292";
    private static final Long TIMEOUT = 30000L;

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
        driver.manage().window().maximize();

        // IMPORTANT set timeout for the http client
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        defaultClient = Configuration.getDefaultApiClient();

        // IMPORTANT set api client timeouts
        defaultClient.setConnectTimeout(TIMEOUT.intValue());
        defaultClient.setWriteTimeout(TIMEOUT.intValue());
        defaultClient.setReadTimeout(TIMEOUT.intValue());

        // IMPORTANT set API KEY and client
        defaultClient.setHttpClient(httpClient);
        defaultClient.setApiKey(API_KEY);

        inboxControllerApi = new InboxControllerApi(defaultClient);
    }

    @Test
    public void userCreatedSuccessfully_when_validateEmail() throws ApiException {
        var user = UserFactory.createDefault();

        InboxDto inbox = inboxControllerApi.createInbox(
                null,
                Arrays.asList(),
                user.getFirstName(),
                "description_example",
                true,
                false,
                null,
                600000L,
                false,
                String.valueOf(InboxDto.InboxTypeEnum.HTTP_INBOX),
                false);

        String email = inbox.getEmailAddress();
        user.setEmail(email);
        var currentTime = OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        driver.navigate().to("https://timelesstales.in/wp-login.php?action=register");
        var usernameInput = driver.findElement(By.id("user_login"));
        var emailInput = driver.findElement(By.id("user_email"));
        var submitButton = driver.findElement(By.id("wp-submit"));
        usernameInput.sendKeys(user.getFirstName());
        emailInput.sendKeys(user.getEmail());
        submitButton.click();

        var waitForControllerApi = new WaitForControllerApi(defaultClient);
        Email receivedEmail = waitForControllerApi
                .waitForLatestEmail(inbox.getId(), TIMEOUT, false, null, currentTime, null, 10000L);
        var emailLink =
                Arrays.stream(receivedEmail.getBody().split(System.getProperty("line.separator")))
                        .filter(l -> l.contains("login=")).findFirst().get();

        driver.navigate().to(emailLink);
        var generatePassButton = driver.findElement(By.xpath("//button[text()='Generate Password']"));
        generatePassButton.click();
        var savePassButton = driver.findElement(By.id("wp-submit"));
        savePassButton.click();
    }

    @Test
    public void interactWithEmailBody() throws ApiException {
        var user = UserFactory.createDefault();

        var inboxControllerApi = new InboxControllerApi(defaultClient);
        var inbox = inboxControllerApi.createInbox(
                null,
                Arrays.asList(),
                user.getFirstName(),
                "description_example",
                true,
                false,
                null,
                600000L,
                false,
                String.valueOf(InboxDto.InboxTypeEnum.HTTP_INBOX),
                false);

        String email = inbox.getEmailAddress();
        user.setEmail(email);

        MailslurpService.sendEmail(inbox, email);

        var currentTime = OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        Email receivedEmail = MailslurpService.waitForLatestEmail(inbox, currentTime);

        MailslurpService.loadEmailBody(driver, receivedEmail.getBody(), true);
        var myAccountLink = driver.findElement(By.xpath("//a[contains(text(), 'My Account')]"));
        myAccountLink.click();

        var wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.urlToBe("https://accounts.lambdatest.com/login"));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
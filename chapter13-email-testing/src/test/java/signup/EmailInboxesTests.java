package signup;

import factories.UserFactory;
import infrastructure.TestmailService;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.mailslurp.apis.*;
import com.mailslurp.clients.*;
import com.mailslurp.models.*;
import utilities.ResourcesReader;
import utilities.TimestampBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class EmailInboxesTests {
    private WebDriver driver;
    private static ApiClient defaultClient = Configuration.getDefaultApiClient();
    private static InboxControllerApi  inboxControllerApi;
    private String API_KEY = "52b1b4382455222921f4d6da2006ebfe5806d58c";
    private static final Long TIMEOUT = 30000L;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();

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

        sendEmail(inbox, email);

        var currentTime = OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        var waitForControllerApi = new WaitForControllerApi(defaultClient);
        Email receivedEmail = waitForControllerApi
                .waitForLatestEmail(inbox.getId(), TIMEOUT, false, null, currentTime, null, 10000L);

        TestmailService.loadEmailBody(driver, receivedEmail.getBody(), false);
        var myAccountLink = driver.findElement(By.xpath("//a[contains(text(), 'My Account')]"));
        myAccountLink.click();

        var wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.urlToBe("https://accounts.lambdatest.com/login"));
    }

    private static void sendEmail(InboxDto inbox, String toEmail) throws ApiException {
        var emailBody = ResourcesReader.getFileAsString(EmailInboxesTests.class, "sample-email.html");
        // send HTML body email
        SendEmailOptions sendEmailOptions = new SendEmailOptions()
                .to(Collections.singletonList(toEmail))
                .subject("HTML BODY email Interaction")
                .body(emailBody);

        inboxControllerApi.sendEmail(inbox.getId(), sendEmailOptions);
    }

    @SneakyThrows
    private static String loadEmailBody(WebDriver driver, String htmlBody) {
        htmlBody = htmlBody.replace("\n", "").replace("\\/", "/").replace("\\\"", "\"");
        String fileName = String.format("%s.html", TimestampBuilder.getGuid());
        var file = writeStringToTempFile(htmlBody);
        driver.get(file.toPath().toUri().toString());

        //driver.get("http://local-folder.lambdatest.com/" + fileName);

        return htmlBody;
    }

    private static File writeStringToTempFile(String fileContent) throws IOException {
        Path tempFile = Files.createTempFile(null, ".html");
        try (var bw = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            bw.write(fileContent);
        }
        return tempFile.toFile();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

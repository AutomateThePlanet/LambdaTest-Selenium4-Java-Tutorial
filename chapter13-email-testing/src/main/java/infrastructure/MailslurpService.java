package infrastructure;

import com.mailslurp.apis.InboxControllerApi;
import com.mailslurp.apis.WaitForControllerApi;
import com.mailslurp.clients.ApiClient;
import com.mailslurp.clients.ApiException;
import com.mailslurp.clients.Configuration;
import com.mailslurp.models.Email;
import com.mailslurp.models.InboxDto;
import com.mailslurp.models.SendEmailOptions;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.SneakyThrows;
import models.emails.EmailsItem;
import models.emails.EmailsResponse;
import okhttp3.OkHttpClient;
import org.openqa.selenium.WebDriver;
import utilities.ResourcesReader;
import utilities.TimestampBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MailslurpService {
    private static ApiClient defaultClient = Configuration.getDefaultApiClient();
    private static InboxControllerApi inboxControllerApi;
    private static String API_KEY = "52b1b4382455222921f4d6da2006ebfe";
    private static final Long TIMEOUT = 30000L;

    static {
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

    @SneakyThrows
    public static InboxDto createInbox(String name) {
        InboxDto inbox = inboxControllerApi.createInbox(
                null,
                Arrays.asList(),
                name,
                "description_example",
                true,
                false,
                null,
                600000L,
                false,
                String.valueOf(InboxDto.InboxTypeEnum.HTTP_INBOX),
                false);

        return inbox;
    }

    @SneakyThrows
    public static Email waitForLatestEmail(InboxDto inbox, OffsetDateTime since) {
        var waitForControllerApi = new WaitForControllerApi(defaultClient);
        Email receivedEmail = waitForControllerApi
                .waitForLatestEmail(inbox.getId(), TIMEOUT, false, null, since, null, 10000L);

        return receivedEmail;
    }

    private static void sendEmail(InboxDto inbox, String toEmail) throws ApiException {
        var emailBody = ResourcesReader.getFileAsString(MailslurpService.class, "sample-email.html");
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

    @SneakyThrows
    public static String loadEmailBody(WebDriver driver, String htmlBody, boolean cloudExecuted) {
        htmlBody = htmlBody.replace("\n", "").replace("\\/", "/").replace("\\\"", "\"");
        String fileName = String.format("%s.html", TimestampBuilder.getGuid());
        var file = writeStringToTempFile(htmlBody);
        driver.get(file.toPath().toUri().toString());

        if (cloudExecuted) {
            driver.get("http://local-folder.lambdatest.com/" + fileName);
        } else {
            driver.get(file.toPath().toUri().toString());
        }

        return htmlBody;
    }

    private static File writeStringToTempFile(String fileContent) throws IOException {
        Path tempFile = Files.createTempFile(null, ".html");
        try (var bw = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            bw.write(fileContent);
        }
        return tempFile.toFile();
    }
}

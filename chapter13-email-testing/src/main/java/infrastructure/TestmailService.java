package infrastructure;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.SneakyThrows;
import models.emails.EmailsItem;
import models.emails.EmailsResponse;
import org.openqa.selenium.WebDriver;
import utilities.TimestampBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class TestmailService {
    private static final String EMAIL_SERVICE_URL = "https://api.testmail.app/";
    private static final String API_KEY = "0877e68e-17fb-4ecf-93e3";
    private static final String NAMESPACE = "ihlzk";

    @SneakyThrows
    public static String loadEmailBody(WebDriver driver, String htmlBody, boolean cloudExecuted) {
        htmlBody = htmlBody.replace("\n", "").replace("\\/", "/").replace("\\\"", "\"");
        String fileName = String.format("%s.html", TimestampBuilder.getGuid());
        var file = writeStringToTempFile(htmlBody);
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

    public static EmailsItem getLastSentEmail(String inboxName) {
        var allEmails = getAllEmails();
        var sortedEmails = allEmails.getEmails().stream().filter(e -> e.getEnvelopeTo().contains(inboxName)).sorted().collect(Collectors.toList());
        return sortedEmails.get((int) (sortedEmails.stream().count() - 1));
    }

    public static List<EmailsItem> getAllEmails(String inboxName) {
        var allEmails = getAllEmails();

        return allEmails.getEmails().stream().filter(e -> e.getEnvelopeTo().contains(inboxName)).collect(Collectors.toList());
    }

    private static EmailsResponse getAllEmails() {
        var emailsResponse = RestAssured.given()
                .baseUri(EMAIL_SERVICE_URL)
                .log().all()
                .contentType(ContentType.JSON)
                .queryParam("apikey", API_KEY)
                .queryParam("namespace", NAMESPACE)
                .queryParam("pretty", "true")
                .get("/api/json/").as(EmailsResponse.class);
        return emailsResponse;
    }
}

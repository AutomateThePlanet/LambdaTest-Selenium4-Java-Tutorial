package captcha;

import com.github.javafaker.Faker;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioProcessingConstants;
import com.microsoft.cognitiveservices.speech.audio.AudioProcessingOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CaptchaHandlingTests {
    private SpeechConfig config = SpeechConfig.fromSubscription("c5f183bc0c084b85a9d61e7bb5be626c", "francecentral");
    private WebDriver driver;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        // use user agent to bypass v3 reCaptcha
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-agent=Mozilla/5.0 (Linux; Android 6.0; HTC One M9 Build/MRA58K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.98 Mobile Safari/537.36");
        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }

    // use these keys to bypass
    // Site Key: 6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI
    // Secret Key: 6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe
    @Test
    public void recaptchaTestAudio() throws ExecutionException, InterruptedException {
        var wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        var faker = new Faker();

        driver.navigate().to("https://demos.bellatrix.solutions/contact-form/");

        var firstName = driver.findElement(By.id("wpforms-3347-field_1"));
        firstName.sendKeys(faker.name().firstName());
        var lastName = driver.findElement(By.id("wpforms-3347-field_1-last"));
        lastName.sendKeys(faker.name().lastName());
        var email = driver.findElement(By.id("wpforms-3347-field_2"));
        email.sendKeys(faker.internet().safeEmailAddress());
        var goldInput = driver.findElement(By.id("wpforms-3347-field_3_3"));
        goldInput.click();
        var session2 = driver.findElement(By.id("wpforms-3347-field_4_2"));
        session2.click();
        var stayOvernightOption = driver.findElement(By.id("wpforms-3347-field_5_1"));
        stayOvernightOption.click();
        var questionInput = driver.findElement(By.id("wpforms-3347-field_7"));
        questionInput.sendKeys("Do you have free rooms?");

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe[@title='reCAPTCHA']")));

        var captchaCheckbox = wait.until(ExpectedConditions.elementToBeClickable((By.xpath("//div[@class='recaptcha-checkbox-border']"))));
        captchaCheckbox.click();

        driver.switchTo().defaultContent();

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe[@title='recaptcha challenge expires in two minutes']")));
        var audioOptionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("recaptcha-audio-button")));
        audioOptionButton.click();


        var audioProcessingOptions = AudioProcessingOptions.create(AudioProcessingConstants.AUDIO_INPUT_PROCESSING_ENABLE_DEFAULT);
        var audioInput = AudioConfig.fromDefaultMicrophoneInput(audioProcessingOptions);
        List<String> recognizedSpeechParts = new ArrayList<>();
        var recognizer = new SpeechRecognizer(config, audioInput);
        {
            recognizer.recognized.addEventListener((s, e) -> {
                if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                    recognizedSpeechParts.add(e.getResult().getText());
                    System.out.println("RECOGNIZED: Text=" + e.getResult().getText());
                }
                else if (e.getResult().getReason() == ResultReason.NoMatch) {
                    System.out.println("NOMATCH: Speech could not be recognized.");
                }
            });

            // Starts continuous recognition. Uses stopContinuousRecognitionAsync() to stop recognition.
            recognizer.startContinuousRecognitionAsync().get();
            var playButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='PLAY']")));
            playButton.click();
            Thread.sleep(10000);
            recognizer.stopContinuousRecognitionAsync().get();
        }

        config.close();
        audioInput.close();
        audioProcessingOptions.close();
        recognizer.close();

        var audioResponseInput = driver.findElement(By.id("audio-response"));
        var captchaText =  String. join("", recognizedSpeechParts);
        audioResponseInput.sendKeys(captchaText);

        var verifyButton = driver.findElement(By.id("recaptcha-verify-button"));
        verifyButton.click();

        driver.switchTo().defaultContent();
        var submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("wpforms-submit-3347")));
        submitButton.click();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

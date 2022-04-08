import com.google.common.collect.ImmutableList;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.devtools.v95.fetch.Fetch;
import org.openqa.selenium.devtools.v95.network.Network;
import org.openqa.selenium.devtools.v95.network.model.BlockedReason;
import org.openqa.selenium.devtools.v95.network.model.ResourceType;
import org.openqa.selenium.devtools.v95.network.model.Response;
import org.openqa.selenium.devtools.v95.security.Security;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.remote.http.Route;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.devtools.v95.network.Network.*;
import static org.openqa.selenium.remote.http.Contents.utf8String;

public class CaptureHttpTrafficTests {
    private final int WAIT_FOR_ELEMENT_TIMEOUT = 30;
    private WebDriver driver;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
        actions = new Actions(driver);
    }

    @Test
    public void basicAuthenticationTest() {
        Predicate<URI> uriPredicate = uri -> uri.getHost().contains("httpbin");

        ((HasAuthentication)driver).register(uriPredicate, UsernameAndPassword.of("user", "passwd"));

        driver.get("http://httpbin.org/basic-auth/user/passwd");

        Assertions.assertTrue(driver.getPageSource().contains("\"authenticated\": true"));
    }

    @Test
    public void networkInterceptionTest(){
        var interceptor = new NetworkInterceptor(
                driver,
                Route.matching(req -> true)
                        .to(() -> req -> new HttpResponse()
                                .setStatus(200)
                                .addHeader("Content-Type", "text/html; charset=utf-8")
                                .setContent(utf8String("You have been hacked!"))));

        driver.get("http://httpbin.org/basic-auth/user/passwd");

        String source = driver.getPageSource();

        Assertions.assertTrue(source.contains("You have been hacked!"));
    }

    @Test
    public void blackHolePatternTest() {
        var devToolsDriver = (HasDevTools)driver;
        DevTools devTools = devToolsDriver.getDevTools();
        devTools.createSession();

        // load insecure certificates
        devTools.send(Security.setIgnoreCertificateErrors(true));

        // block all CSS
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.send(Network.setBlockedURLs(ImmutableList.of("*.png","*.css")));

        devTools.addListener(loadingFailed(), loadingFailed -> {

            if (loadingFailed.getType().equals(ResourceType.STYLESHEET)) {
                assertEquals(loadingFailed.getBlockedReason(), BlockedReason.INSPECTOR);
            }

            else if (loadingFailed.getType().equals(ResourceType.IMAGE)) {
                assertEquals(loadingFailed.getBlockedReason(), BlockedReason.INSPECTOR);
            }

        });

        // block URLs
        devTools.send(Network.setBlockedURLs(List.of("https://demos.bellatrix.solutions/32312419215-324x324.jpg")));

        devTools.addListener(loadingFailed(), loadingFailed -> {
            System.out.println("Blocking reason: " + loadingFailed.getBlockedReason().get());
        });

        driver.get("https://demos.bellatrix.solutions/");
    }

    @Test
    public void mockingAPICallsTest(){
        var devToolsDriver = (HasDevTools)driver;
        DevTools devTools = devToolsDriver.getDevTools();
        devTools.createSession();

        // mock API calls
        devTools.send(Fetch.enable(Optional.empty(), Optional.empty()));

        devTools.addListener(Fetch.requestPaused(), req -> {
            if (req.getRequest().getUrl().contains("https://demos.telerik.com/kendo-ui/service/Northwind.svc/Orders")) {
                String mockUrl = req.getRequest().getUrl().replace("top=20", "top=5");
                devTools.send(Fetch.continueRequest(req.getRequestId(), Optional.of(mockUrl), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

            } else {
                devTools.send(Fetch.continueRequest(req.getRequestId(), Optional.of(req.getRequest().getUrl()), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
            }
        });

        driver.get("https://demos.telerik.com/aspnet-mvc/grid/odata");
    }

    @Test
    public void verifyWebSocketOperationTest() throws InterruptedException {
        var devToolsDriver = (HasDevTools)driver;
        DevTools devTools = devToolsDriver.getDevTools();
        devTools.createSession();

        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(webSocketCreated(), e -> {
            System.out.print("Created");
            System.out.print(e.getUrl());
            System.out.print(e.getInitiator().get().getUrl());
            System.out.print(e.getInitiator().get().getLineNumber());
        });

        devTools.addListener(webSocketFrameReceived(), e -> {
            System.out.print("Received");
            System.out.print(e.getResponse().getPayloadData());
            System.out.print(e.getResponse().getOpcode());
            System.out.print(e.getResponse().getMask());
        });

        devTools.addListener(webSocketFrameError(), e -> {
            System.out.print(e.getErrorMessage());
        });

        devTools.addListener(webSocketClosed(), e -> {
            System.out.print("Closed");
            System.out.print(e.getTimestamp());
        });

        driver.get("https://www.piesocket.com/websocket-tester");
        var button = driver.findElement(By.xpath("//button[@type='submit']"));
        button.click();
        Thread.sleep(20000);
    }

    @Test
    public void captureHttpTrafficTest(){
        var devToolsDriver = (HasDevTools)driver;
        DevTools devTools = devToolsDriver.getDevTools();
        devTools.createSession();

        // capture HTTP traffic
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        List<Response> capturedResponses = Collections.synchronizedList(new ArrayList<>());
        devTools.addListener(Network.responseReceived(), responseReceived -> {
            capturedResponses.add(responseReceived.getResponse());
        });

        assertNoErrorCodes(capturedResponses);
    }

    public void assertNoErrorCodes(List<Response> capturedResponses) {
        Boolean areThereErrorCodes = capturedResponses.stream().anyMatch(r -> r.getStatus() > 400 && r.getStatus() < 599);
        Assertions.assertFalse(areThereErrorCodes, "Error codes detected on the page.");
    }

    public void assertRequestMade(List<Response> capturedResponses, String url) {
        Boolean areRequestsMade = capturedResponses.stream().anyMatch(r -> r.getUrl().contains(url));
        Assertions.assertTrue(areRequestsMade, String.format("Request %s was not made.", url));
    }

    public void assertRequestNotMade(List<Response> capturedResponses, String url) {
        Boolean areRequestsMade = capturedResponses.stream().anyMatch(r -> r.getUrl().contains(url));
        Assertions.assertFalse(areRequestsMade, String.format("Request %s was made.", url));
    }

    public void assertNoLargeImagesRequested(List<Response> capturedResponses, int contentLength) {
        Boolean areThereLargeImages = capturedResponses.stream().anyMatch(r -> r.getMimeType() == ResourceType.IMAGE.toString() &&
                r.getHeaders() != null && Integer.parseInt(r.getHeaders().get("Content-Length").toString()) < contentLength);
        Assertions.assertFalse(areThereLargeImages, String.format("Larger than %s images detected.", contentLength));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
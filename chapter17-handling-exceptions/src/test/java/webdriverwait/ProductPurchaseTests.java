/*
 * Copyright 2021 Automate The Planet Ltd.
 * Author: Anton Angelov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package webdriverwait;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v102.security.Security;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/*
 * The order of test execution is important. The tests should be executed in the following order:
 * completePurchaseSuccessfully_whenNewClient
 * completePurchaseSuccessfully_whenExistingClient
 * correctOrderDataDisplayed_WhenNavigateToMyAccountOrderSection
 *
 * The tests may fail because the hard-coded pauses were not enough.
 * This is the expected behavior showing that this is not the best practice.
 */
public class ProductPurchaseTests {
    private WebDriver driver;
    private static String purchaseEmail;
    private static String purchaseOrderNumber;

    @BeforeEach
    public void testInit() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    @AfterEach
    public void testCleanup() throws InterruptedException {
        driver.quit();
    }

    @Test
    @Order(1)
    public void completePurchaseSuccessfully_whenNewClient() throws InterruptedException {
        driver.navigate().to("http://demos.bellatrix.solutions/");

        // throw new - AnalyzedTestException to show how it is printed both ways. Make screenshot.

        var addToCartFalcon9 = findElement(By.cssSelector("[data-product_id*='28']"));
        addToCartFalcon9.click();
        var viewCartButton = findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();

        var couponCodeTextField = findElement(By.id("coupon_code"));
        couponCodeTextField.clear();
        couponCodeTextField.sendKeys("happybirthday");
        var applyCouponButton = findElement(By.cssSelector("[value*='Apply coupon']"));
        applyCouponButton.click();
        Thread.sleep(4000);
        var messageAlert = findElement(By.cssSelector("[class*='woocommerce-message']"));
        Assertions.assertEquals(messageAlert.getText(), "Coupon code applied successfully.");

        var quantityBox = findElement(By.cssSelector("[class*='input-text qty text']"));
        quantityBox.clear();
        quantityBox.sendKeys("2");

        var updateCart = findElement(By.cssSelector("[value*='Update cart']"));
        updateCart.click();
        Thread.sleep(4000);
        var totalSpan = findElement(By.xpath("//*[@class='order-total']//span"));
        Assertions.assertEquals("114.00€", totalSpan.getText());

         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // SELENIUM EXCEPTIONS EXPLANATION AND EXAMPLES:

        // ElementClickInterceptedException
        // ElementNotInteractableException
        try {
            var updateCart1 = findElement(By.cssSelector("[value*='Update cart']"));
            updateCart.click();
        } catch (ElementClickInterceptedException e) {
            System.out.println(e.getMessage());
        } catch (TimeoutException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


        // ElementNotSelectableException
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[value*='Update cart']")));

        // ElementNotVisibleException

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[value*='Update cart']")));
        // InsecureCertificateException

        var devToolsDriver = (HasDevTools)driver;
        DevTools devTools = devToolsDriver.getDevTools();
        devTools.createSession();
        devTools.send(Security.setIgnoreCertificateErrors(true));

        var firefoxOptions = new FirefoxOptions();
        var firefoxProfile = new FirefoxProfile();
        firefoxProfile.setAcceptUntrustedCertificates(true);
        firefoxOptions.setProfile(firefoxProfile);

        ChromeOptions option = new ChromeOptions();
        option.setAcceptInsecureCerts(true);

        // UnableToSetCookieException
        // InvalidCookieDomainException
        Cookie csrfCookie = new Cookie.Builder("firstName", "anton").domain("stackoverflow").build();
        driver.manage().addCookie(csrfCookie);

        // InvalidCoordinatesException
        Actions actions = new Actions(driver);
        actions.moveToElement(updateCart).moveByOffset(111, 222).click().perform();

        // InvalidElementStateException
        // InvalidSessionIdException
        // InvalidSwitchToTargetException
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("active-profile")));
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        // JavascriptException
        // MoveTargetOutOfBoundsException
        actions.moveToElement(updateCart).moveByOffset(111, 222).click().perform();

        // NoAlertPresentException
        wait.until(ExpectedConditions.alertIsPresent());
        try {
            driver.switchTo().alert().accept();
        } catch (NoAlertPresentException e) {
            e.printStackTrace();
        }
        // NoSuchAttributeException
        var hiddenValue = updateCart.getAttribute("hidden-value");
        wait.until(ExpectedConditions.attributeToBe(By.cssSelector("[value*='Update cart']"), "hidden-value", "mySecret"));
        wait.until(ExpectedConditions.attributeToBe(updateCart, "hidden-value", "mySecret"));
        wait.until(ExpectedConditions.attributeToBeNotEmpty(updateCart, "hidden-value"));
        wait.until(ExpectedConditions.attributeContains(updateCart, "hidden-value", "secret"));

        // NoSuchCookieException
        wait.until(d -> d.manage().getCookieNamed("hidden-secret") != null);

        // NoSuchElementException
        // InvalidSelectorException
        var messageAlert1 = findElement(By.cssSelector("[+class*='woocommerce-message']"));


        // NoSuchFrameException
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("active-profile")));

        // NoSuchWindowException
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        for (String handle : driver.getWindowHandles()) {
            try {
                driver.switchTo().window(handle);
            } catch (NoSuchWindowException e) {
                e.printStackTrace();

                throw e;
            }
        }
        // RemoteDriverServerException
        // SessionNotCreatedException
        // StaleElementReferenceException

        wait.until(d -> ((JavascriptExecutor)d).executeScript("return jQuery.active").toString() == "0");
        // wait for ajax
        // wait for animations

        // TimeoutException
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)) ;
        wait.until(d -> ((JavascriptExecutor)d).executeScript("return document.readyState").equals("complete"));



        // UnexpectedAlertPresentException
        // WebDriverException


        // Lambda Test to show exceptions
        // Hyper Execute + stack trace












        var proceedToCheckout = findElement(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        proceedToCheckout.click();

        var billingFirstName = findElement(By.id("billing_first_name"));
        billingFirstName.sendKeys("Anton");
        var billingLastName = findElement(By.id("billing_last_name"));
        billingLastName.sendKeys("Angelov");
        var billingCompany = findElement(By.id("billing_company"));
        billingCompany.sendKeys("Space Flowers");
        var billingCountryWrapper = findElement(By.id("select2-billing_country-container"));
        billingCountryWrapper.click();
        var billingCountryFilter = findElement(By.className("select2-search__field"));
        billingCountryFilter.sendKeys("Germany");
        var germanyOption = findElement(By.xpath("//*[contains(text(),'Germany')]"));
        germanyOption.click();
        var billingAddress1 = findElement(By.id("billing_address_1"));
        billingAddress1.sendKeys("1 Willi Brandt Avenue Tiergarten");
        var billingAddress2 = findElement(By.id("billing_address_2"));
        billingAddress2.sendKeys("Lotzowplatz 17");
        var billingCity = findElement(By.id("billing_city"));
        billingCity.sendKeys("Berlin");
        var billingZip = findElement(By.id("billing_postcode"));
        billingZip.clear();
        billingZip.sendKeys("10115");
        var billingPhone = findElement(By.id("billing_phone"));
        billingPhone.sendKeys("+00498888999281");
        var billingEmail = findElement(By.id("billing_email"));
        billingEmail.sendKeys("info@berlinspaceflowers.com");
        purchaseEmail = "info@berlinspaceflowers.com";

        // This pause will be removed when we introduce a logic for waiting for AJAX requests.
        Thread.sleep(5000);
        var placeOrderButton = findElement(By.id("place_order"));
        placeOrderButton.click();

        Thread.sleep(10000);
        var receivedMessage = findElement(By.xpath("/html/body/div[1]/div/div/div/main/div/header/h1"));
        Assertions.assertEquals(receivedMessage.getText(), "Order received");
    }

    private String getUserPasswordFromDb(String userName) {
        return "@purISQzt%%DYBnLCIhaoG6$";
    }

    private WebElement findElement(By by) {
        var webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    private List<WebElement> findElements(By by) {
        var webDriverWait = new WebDriverWait(driver,  Duration.ofSeconds(30));
        return webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }
}
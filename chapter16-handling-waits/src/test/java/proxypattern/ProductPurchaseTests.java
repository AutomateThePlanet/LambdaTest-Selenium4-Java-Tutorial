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

package proxypattern;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private WebDriverProxy proxyDriver;
    private static String purchaseEmail;
    private static String purchaseOrderNumber;

    @BeforeEach
    public void testInit() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        proxyDriver = new WebDriverProxy(driver);
    }

    @AfterEach
    public void testCleanup() throws InterruptedException {
        driver.quit();
    }

    @Test
    @Order(1)
    public void completePurchaseSuccessfully_whenNewClient() throws InterruptedException {
        proxyDriver.navigate().to("http://demos.bellatrix.solutions/");
        var addToCartFalcon9 = proxyDriver.findElement(By.cssSelector("[data-product_id*='28']"));
        addToCartFalcon9.click();
        var viewCartButton = proxyDriver.findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();

        var couponCodeTextField = proxyDriver.findElement(By.id("coupon_code"));
        couponCodeTextField.clear();
        couponCodeTextField.sendKeys("happybirthday");
        var applyCouponButton = proxyDriver.findElement(By.cssSelector("[value*='Apply coupon']"));
        applyCouponButton.click();
        Thread.sleep(4000);
        var messageAlert = proxyDriver.findElement(By.cssSelector("[class*='woocommerce-message']"));
        Assertions.assertEquals(messageAlert.getText(), "Coupon code applied successfully.");

        var quantityBox = proxyDriver.findElement(By.cssSelector("[class*='input-text qty text']"));
        quantityBox.clear();
        quantityBox.sendKeys("2");

        var updateCart = proxyDriver.findElement(By.cssSelector("[value*='Update cart']"));
        updateCart.click();
        Thread.sleep(4000);
        var totalSpan = proxyDriver.findElement(By.xpath("//*[@class='order-total']//span"));
        Assertions.assertEquals("114.00€", totalSpan.getText());

        var proceedToCheckout = proxyDriver.findElement(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        proceedToCheckout.click();

        var billingFirstName = proxyDriver.findElement(By.id("billing_first_name"));
        billingFirstName.sendKeys("Anton");
        var billingLastName = proxyDriver.findElement(By.id("billing_last_name"));
        billingLastName.sendKeys("Angelov");
        var billingCompany = proxyDriver.findElement(By.id("billing_company"));
        billingCompany.sendKeys("Space Flowers");
        var billingCountryWrapper = proxyDriver.findElement(By.id("select2-billing_country-container"));
        billingCountryWrapper.click();
        var billingCountryFilter = proxyDriver.findElement(By.className("select2-search__field"));
        billingCountryFilter.sendKeys("Germany");
        var germanyOption = proxyDriver.findElement(By.xpath("//*[contains(text(),'Germany')]"));
        germanyOption.click();
        var billingAddress1 = proxyDriver.findElement(By.id("billing_address_1"));
        billingAddress1.sendKeys("1 Willi Brandt Avenue Tiergarten");
        var billingAddress2 = proxyDriver.findElement(By.id("billing_address_2"));
        billingAddress2.sendKeys("Lotzowplatz 17");
        var billingCity = proxyDriver.findElement(By.id("billing_city"));
        billingCity.sendKeys("Berlin");
        var billingZip = proxyDriver.findElement(By.id("billing_postcode"));
        billingZip.clear();
        billingZip.sendKeys("10115");
        var billingPhone = proxyDriver.findElement(By.id("billing_phone"));
        billingPhone.sendKeys("+00498888999281");
        var billingEmail = proxyDriver.findElement(By.id("billing_email"));
        billingEmail.sendKeys("info@berlinspaceflowers.com");
        purchaseEmail = "info@berlinspaceflowers.com";

        // This pause will be removed when we introduce a logic for waiting for AJAX requests.
        Thread.sleep(5000);
        var placeOrderButton = proxyDriver.findElement(By.id("place_order"));
        placeOrderButton.click();

        Thread.sleep(10000);
        var receivedMessage = proxyDriver.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div/header/h1"));
        Assertions.assertEquals(receivedMessage.getText(), "Order received");
    }

    @Test()
    @Order(2)
    public void completePurchaseSuccessfully_whenExistingClient() throws InterruptedException {
        driver.navigate().to("http://demos.bellatrix.solutions/");

        var addToCartFalcon9 = proxyDriver.findElement(By.cssSelector("[data-product_id*='28']"));
        addToCartFalcon9.click();
        var viewCartButton = proxyDriver.findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();

        var couponCodeTextField = proxyDriver.findElement(By.id("coupon_code"));
        couponCodeTextField.clear();
        couponCodeTextField.sendKeys("happybirthday");
        var applyCouponButton = proxyDriver.findElement(By.cssSelector("[value*='Apply coupon']"));
        applyCouponButton.click();
        var messageAlert = proxyDriver.findElement(By.cssSelector("[class*='woocommerce-message']"));
        Thread.sleep(4000);
        Assertions.assertEquals(messageAlert.getText(), "Coupon code applied successfully.");

        var quantityBox = proxyDriver.findElement(By.cssSelector("[class*='input-text qty text']"));
        quantityBox.clear();
        quantityBox.sendKeys("2");
        var updateCart = proxyDriver.findElement(By.cssSelector("[value*='Update cart']"));
        updateCart.click();
        Thread.sleep(4000);
        var totalSpan = proxyDriver.findElement(By.xpath("//*[@class='order-total']//span"));
        Assertions.assertEquals(totalSpan.getText(), "114.00€");

        var proceedToCheckout = proxyDriver.findElement(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        proceedToCheckout.click();

        var loginHereLink = proxyDriver.findElement(By.linkText("Click here to login"));
        loginHereLink.click();
        var userName = proxyDriver.findElement(By.id("username"));
        userName.sendKeys(purchaseEmail);
        var password = proxyDriver.findElement(By.id("password"));
        password.sendKeys(GetUserPasswordFromDb(purchaseEmail));
        var loginButton = proxyDriver.findElement(By.xpath("//button[@name='login']"));
        loginButton.click();

        // This pause will be removed when we introduce a logic for waiting for AJAX requests.
        Thread.sleep(5000);
        var placeOrderButton = proxyDriver.findElement(By.id("place_order"));
        placeOrderButton.click();

        var receivedMessage = proxyDriver.findElement(By.xpath("//h1[text() = 'Order received']"));
        Assertions.assertEquals(receivedMessage.getText(), "Order received");

        var orderNumber = proxyDriver.findElement(By.xpath("//*[@id='post-7']//li[1]/strong"));
        purchaseOrderNumber = orderNumber.getText();
    }

    @Test
    @Order(3)
    public void correctOrderDataDisplayed_whenNavigateToMyAccountOrderSection() throws InterruptedException {
        driver.navigate().to("http://demos.bellatrix.solutions/");

        var myAccountLink = proxyDriver.findElement(By.linkText("My account"));
        myAccountLink.click();
        var userName = proxyDriver.findElement(By.id("username"));
        Thread.sleep(4000);
        userName.sendKeys(purchaseEmail);
        var password = proxyDriver.findElement(By.id("password"));
        password.sendKeys(GetUserPasswordFromDb(GetUserPasswordFromDb(purchaseEmail)));
        var loginButton = proxyDriver.findElement(By.xpath("//button[@name='login']"));
        loginButton.click();

        var orders = proxyDriver.findElement(By.linkText("Orders"));
        orders.click();

        var viewButtons = proxyDriver.findElements(By.linkText("View"));
        viewButtons.get(0).click();

        var orderName = proxyDriver.findElement(By.xpath("//h1"));
        String expectedMessage = String.format("Order #%s", purchaseOrderNumber);
        Assertions.assertEquals(expectedMessage, orderName.getText());
    }

    private String GetUserPasswordFromDb(String userName)
    {
        return "@purISQzt%%DYBnLCIhaoG6$";
    }
}
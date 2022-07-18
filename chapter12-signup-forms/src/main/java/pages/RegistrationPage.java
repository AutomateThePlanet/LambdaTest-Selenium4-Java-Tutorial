package pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import models.User;

public class RegistrationPage {
    private final WebDriver driver;

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement mainHeading() {
        return driver.findElement(By.tagName("h1"));
    }

    public WebElement mainErrorSummary() {
        return driver.findElement(By.className("alert-dismissible"));
    }

    public WebElement loginPageLink() {
        return driver.findElement(By.xpath("//h1/following-sibling::p/a"));
    }

    public WebElement fistNameInput() {
        return driver.findElement(By.id("input-firstname"));
    }

    public WebElement fistNameLabel() {
        return driver.findElement(By.xpath("//label[@for='input-firstname']"));
    }

    public WebElement lastNameInput() {
        return driver.findElement(By.id("input-lastname"));
    }

    public WebElement lastNameLabel() {
        return driver.findElement(By.xpath("//label[@for='input-lastname']"));
    }

    public WebElement emailInput() {
        return driver.findElement(By.id("input-email"));
    }

    public WebElement emailLabel() {
        return driver.findElement(By.xpath("//label[@for='input-email']"));
    }

    public WebElement telephoneInput() {
        return driver.findElement(By.id("input-telephone"));
    }

    public WebElement telephoneLabel() {
        return driver.findElement(By.xpath("//label[@for='input-telephone']"));
    }

    public WebElement passwordInput() {
        return driver.findElement(By.id("input-password"));
    }

    public WebElement passwordLabel() {
        return driver.findElement(By.xpath("//label[@for='input-password']"));
    }

    public WebElement passwordConfirmInput() {
        return driver.findElement(By.id("input-confirm"));
    }

    public WebElement passwordConfirmLabel() {
        return driver.findElement(By.xpath("//label[@for='input-confirm']"));
    }

    public WebElement newsletterSubscribeYes() {
        return driver.findElement(By.id("input-newsletter-yes"));
    }

    public WebElement newsletterSubscribeNo() {
        return driver.findElement(By.id("input-newsletter-no"));
    }

    public WebElement subscribeLabel() {
        return driver.findElement(By.xpath("//*[@id='content']/form/fieldset[3]/div/label"));
    }

    public WebElement privacyPolicyCheckbox() {
        return driver.findElement(By.xpath("//input[@id='input-agree']/following-sibling::label"));
    }

    public WebElement privacyPolicyLink() {
        return driver.findElement(By.xpath("//a[@class='agree']"));
    }

    public WebElement continueButton() {
        return driver.findElement(By.xpath("//input[@value='Continue']"));
    }

    public void open() {
        driver.navigate().to("https://ecommerce-playground.lambdatest.io/index.php?route=account/register");
    }

    public void openPrivacyPolicy() {
        privacyPolicyLink().click();
    }

    public void assertFirstNameValidation() {
        var actualError = getErrorMessage("First Name");
        Assertions.assertEquals("First Name must be between 1 and 32 characters!", actualError);
    }

    public void assertLastNameValidation() {
        var actualError = getErrorMessage("Last Name");
        Assertions.assertEquals("Last Name must be between 1 and 32 characters!", actualError);
    }

    public void assertEmailValidation() {
        var actualError = getErrorMessage("E-Mail");
        Assertions.assertEquals("E-Mail Address does not appear to be valid!", actualError);
    }

    public void assertTelephoneValidation() {
        var actualError = getErrorMessage("Telephone");
        Assertions.assertEquals("Telephone must be between 3 and 32 characters!", actualError);
    }

    public void assertPasswordValidation() {
        var actualError = getErrorMessage("Password");
        Assertions.assertEquals("Password must be between 4 and 20 characters!", actualError);
    }

    public void assertPasswordConfirmationMismatchValidation() {
        var actualError = getErrorMessage("Password Confirm");
        Assertions.assertEquals("Password confirmation does not match password!", actualError);
    }

    public void assertPrivacyPolicyAgreementValidation() {
        var actualError = getErrorMessage("Password Confirm");
        Assertions.assertEquals(" Warning: You must agree to the Privacy Policy!", mainErrorSummary().getText());
    }

    public String getErrorMessage(String inputLabel) {
        var xpathLocator = String.format("//label[text()='%s']//following-sibling::div/div", inputLabel);
        return driver.findElement(By.xpath(xpathLocator)).getText();
    }

    public void assertPlaceholder(String expectedText, WebElement element) {
        var actualPlaceHolder = getPlaceholder(element);
        Assertions.assertEquals(expectedText, actualPlaceHolder);
    }

    public String getPlaceholder(WebElement element) {
        return element.getAttribute("placeholder");
    }

    public void register(User user, Boolean useEnter) {
        if (!user.getFirstName().isEmpty()) {
            fistNameInput().sendKeys(user.getFirstName());
        }

        if (!user.getLastName().isEmpty()) {
            lastNameInput().sendKeys(user.getLastName());
        }

        if (!user.getEmail().isEmpty()) {
            emailInput().sendKeys(user.getEmail());
        }

        if (!user.getTelephone().isEmpty()) {
            telephoneInput().sendKeys(user.getTelephone());
        }

        if (!user.getPassword().isEmpty()) {
            passwordInput().sendKeys(user.getPassword());
        }

        if (!user.getPasswordConfirm().isEmpty()) {
            passwordConfirmInput().sendKeys(user.getPasswordConfirm());
        }

        if (user.getShouldSubscribe() && !newsletterSubscribeYes().isSelected()) {
            newsletterSubscribeYes().click();
        } else if (!user.getShouldSubscribe() && !newsletterSubscribeNo().isSelected()) {
            newsletterSubscribeNo().click();
        }

        if (user.getAgreePrivacyPolicy()) {
           privacyPolicyCheckbox().click();
        }

        if (useEnter) {
            continueButton().sendKeys(Keys.ENTER);
        } else {
            continueButton().click();
        }
    }
}

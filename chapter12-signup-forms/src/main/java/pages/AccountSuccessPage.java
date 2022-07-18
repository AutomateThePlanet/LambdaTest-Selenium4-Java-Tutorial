package pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AccountSuccessPage {
    private final WebDriver driver;

    public AccountSuccessPage(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement mainHeading() {
        return driver.findElement(By.tagName("h1"));
    }

    public void assertAccountCreatedSuccessfully() {
        Assertions.assertEquals("Your Account Has Been Created!", mainHeading().getText().trim());
    }
}
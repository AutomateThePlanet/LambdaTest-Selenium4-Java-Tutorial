package decoratorpattern;

import org.openqa.selenium.By;

public class LogElement extends ElementDecorator {

    protected LogElement(Element element) {
        super(element);
    }

    @Override
    public By getBy() {
        return element.getBy();
    }

    @Override
    public String getText() {
        System.out.print(String.format("Element Text = %s", element.getText()));
        return element.getText();
    }

    @Override
    public Boolean isEnabled() {
        System.out.print(String.format("Element Enabled = %b", element.isEnabled()));
        return element.isEnabled();
    }

    @Override
    public Boolean isDisplayed() {
        System.out.print(String.format("Element Displayed = %b", element.isDisplayed()));
        return element.isDisplayed();
    }

    @Override
    public void typeText(String text) throws InterruptedException {
        System.out.print(String.format("Type Text = = %s", text));
        element.typeText(text);
    }

    @Override
    public void click() {
        System.out.print("Element Clicked");
        element.click();
    }

    @Override
    public String getAttribute(String attributeName) {
        System.out.print("Element Clicked");
        return element.getAttribute(attributeName);
    }
}

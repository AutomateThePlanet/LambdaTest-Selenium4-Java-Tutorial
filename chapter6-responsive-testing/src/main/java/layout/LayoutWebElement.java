package layout;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LayoutWebElement extends LayoutComponentValidationsBuilder {
    @Getter @Setter private final WebElement webElement;

    public LayoutWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    @Override
    public String getComponentName() {
        return webElement.getAccessibleName();
    }

    @Override
    public Point getLocation() {
        return webElement.getLocation();
    }

    @Override
    public Dimension getSize() {
        return webElement.getSize();
    }

    public void assertBackgroundColor(String expectedBackgroundColor) {
        var actualColor = webElement.getCssValue("background-color");
        assertEquals(expectedBackgroundColor, actualColor);
    }

    public void assertBorderColor(String expectedBorderColor) {
        assertEquals(expectedBorderColor, webElement.getCssValue("border-color"));
    }

    public void assertColor(String expectedColor) {
        assertEquals(expectedColor, webElement.getCssValue("color"));
    }

    public void assertFontFamily(String expectedFontFamily) {
        assertEquals(expectedFontFamily, webElement.getCssValue("font-family"));
    }

    public void assertFontWeight(String expectedFontWeight) {
        assertEquals(expectedFontWeight, webElement.getCssValue("font-weight"));
    }

    public void assertFontSize(String expectedFontSize) {
        assertEquals(expectedFontSize, webElement.getCssValue("font-size"));
    }

    public void assertTextAlign(String expectedTextAlign) {
        assertEquals(expectedTextAlign, webElement.getCssValue("text-align"));
    }

    public void assertVerticalAlign(String expectedVerticalAlign) {
        assertEquals(expectedVerticalAlign, webElement.getCssValue("vertical-align"));
    }
}

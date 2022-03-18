package layout;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LayoutWebSelect extends LayoutComponentValidationsBuilder {
    @Getter @Setter private final Select webElement;

    public LayoutWebSelect(Select webElement) {
        this.webElement = webElement;
    }

    @Override
    public String getComponentName() {
        return webElement.getWrappedElement().getAccessibleName();
    }

    @Override
    public Point getLocation() {
        return webElement.getWrappedElement().getLocation();
    }

    @Override
    public Dimension getSize() {
        return webElement.getWrappedElement().getSize();
    }

    public void assertBackgroundColor(String expectedBackgroundColor) {
        var actualColor = webElement.getWrappedElement().getCssValue("background-color");
        assertEquals(expectedBackgroundColor, actualColor);
    }

    public void assertBorderColor(String expectedBorderColor) {
        assertEquals(expectedBorderColor, webElement.getWrappedElement().getCssValue("border-color"));
    }

    public void assertColor(String expectedColor) {
        assertEquals(expectedColor, webElement.getWrappedElement().getCssValue("color"));
    }

    public void assertFontFamily(String expectedFontFamily) {
        assertEquals(expectedFontFamily, webElement.getWrappedElement().getCssValue("font-family"));
    }

    public void assertFontWeight(String expectedFontWeight) {
        assertEquals(expectedFontWeight, webElement.getWrappedElement().getCssValue("font-weight"));
    }

    public void assertFontSize(String expectedFontSize) {
        assertEquals(expectedFontSize, webElement.getWrappedElement().getCssValue("font-size"));
    }

    public void assertTextAlign(String expectedTextAlign) {
        assertEquals(expectedTextAlign, webElement.getWrappedElement().getCssValue("text-align"));
    }

    public void assertVerticalAlign(String expectedVerticalAlign) {
        assertEquals(expectedVerticalAlign, webElement.getWrappedElement().getCssValue("vertical-align"));
    }
}

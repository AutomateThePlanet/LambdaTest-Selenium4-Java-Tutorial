package layout;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

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
}

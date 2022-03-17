package layout;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

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
}

package layout;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

public class Screen implements LayoutComponent {
    private final Point location;
    private final Dimension size;

    Screen(WebDriver driver) {
        location = new Point(0, 0);
        size = driver.manage().window().getSize();
    }

    @Override
    public String getComponentName() {
        return "Screen";
    }

    @Override
    public Point getLocation() {
        return location;
    }

    @Override
    public Dimension getSize() {
        return size;
    }
}

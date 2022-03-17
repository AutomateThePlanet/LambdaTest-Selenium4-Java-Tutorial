package layout;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

public class SpecialComponents {
    public static LayoutComponent getViewport(WebDriver driver) {
        return new Viewport(driver);
    }

    public static LayoutComponent getScreen(WebDriver driver) {
        return new Screen(driver);
    }
}

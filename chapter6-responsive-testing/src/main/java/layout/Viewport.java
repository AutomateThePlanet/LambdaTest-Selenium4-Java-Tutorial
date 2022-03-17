package layout;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

public class Viewport implements LayoutComponent {
    private final Point location;
    private final Dimension size;

    Viewport(WebDriver driver) {
        var javaScriptService = (JavascriptExecutor)driver;
        location = new Point(0, 0);
        var viewportWidth = Integer.parseInt(javaScriptService.executeScript("return Math.max(document.documentElement.clientWidth, window.innerWidth || 0);").toString());
        var viewportHeight = Integer.parseInt(javaScriptService.executeScript("return Math.max(document.documentElement.clientHeight, window.innerHeight || 0);").toString());
        size = new Dimension(viewportWidth, viewportHeight);
    }

    @Override
    public String getComponentName() {
        return "Viewport";
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

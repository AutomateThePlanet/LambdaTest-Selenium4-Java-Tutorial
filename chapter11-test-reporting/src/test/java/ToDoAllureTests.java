import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

@Epic("Selenium 4 Series")
@Feature("Junit 5 Data-driven Tests Selenium 4")
public class ToDoAllureTests {
    private final int WAIT_FOR_ELEMENT_TIMEOUT = 30;
    private WebDriver driver;
    private WebDriverWait webDriverWait;
    private Actions actions;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
        actions = new Actions(driver);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @Story("Base support for bdd annotations")
    void isBlank_ShouldReturnTrueForNullAndEmptyStrings(String input) {
        Assertions.assertTrue(input == null);
    }

    @Story("Advanced support for bdd annotations")
    @ParameterizedTest(name = "{index}. verify todo list created successfully when technology = {0}")
    @ValueSource(strings = {
            "Backbone.js",
            "AngularJS",
            "React",
            "Vue.js",
            "CanJS",
            "Ember.js",
            "KnockoutJS",
            "Marionette.js",
            "Polymer",
            "Angular 2.0",
            "Dart",
            "Elm",
            "Closure",
            "Vanilla JS",
            "jQuery",
            "cujoJS",
            "Spine",
            "Dojo",
            "Mithril",
            "Kotlin + React",
            "Firebase + AngularJS",
            "Vanilla ES6"
    })
    @NullAndEmptySource
    public void verifyToDoListCreatedSuccessfully_withParams(String technology){
        driver.navigate().to("https://todomvc.com/");
        openTechnologyApp(technology);
        addNewToDoItem("Clean the car");
        addNewToDoItem("Clean the house");
        addNewToDoItem("Buy Ketchup");
        getItemCheckbox("Buy Ketchup").click();

        assertLeftItems(2);

        // TODO: add attachment?
    }

    @ParameterizedTest
    @EnumSource(WebTechnology.class)
    public void verifyToDoListCreatedSuccessfully_withEnum(WebTechnology technology){
        driver.navigate().to("https://todomvc.com/");
        openTechnologyApp(technology.getTechnologyName());
        addNewToDoItem("Clean the car");
        addNewToDoItem("Clean the house");
        addNewToDoItem("Buy Ketchup");
        getItemCheckbox("Buy Ketchup").click();

        assertLeftItems(2);
    }

    // Enum filter - data driven
    @ParameterizedTest
    @EnumSource(value = WebTechnology.class, names = {"BACKBONEJS", "ANGULARJS", "EMBERJS", "KNOCKOUTJS"})
    public void verifyToDoListCreatedSuccessfully_withEnumFilter(WebTechnology technology){
        driver.navigate().to("https://todomvc.com/");
        openTechnologyApp(technology.getTechnologyName());
        addNewToDoItem("Clean the car");
        addNewToDoItem("Clean the house");
        addNewToDoItem("Buy Ketchup");
        getItemCheckbox("Buy Ketchup").click();

        assertLeftItems(2);
    }

    // Enum filter exclude - data driven
    @ParameterizedTest
    @EnumSource(value = WebTechnology.class, names = {"BACKBONEJS", "ANGULARJS", "EMBERJS", "KNOCKOUTJS"}, mode = EnumSource.Mode.EXCLUDE)
    public void verifyToDoListCreatedSuccessfully_withEnumFilterExclude(WebTechnology technology){
        driver.navigate().to("https://todomvc.com/");
        openTechnologyApp(technology.getTechnologyName());
        addNewToDoItem("Clean the car");
        addNewToDoItem("Clean the house");
        addNewToDoItem("Buy Ketchup");
        getItemCheckbox("Buy Ketchup").click();

        assertLeftItems(2);
    }

    // Enum filter exclude - data driven
    @ParameterizedTest
    @EnumSource(value = WebTechnology.class, names = {".+JS"}, mode = EnumSource.Mode.EXCLUDE)
    public void verifyToDoListCreatedSuccessfully_withEnumFilterExcludeRegex(WebTechnology technology){
        driver.navigate().to("https://todomvc.com/");
        openTechnologyApp(technology.getTechnologyName());
        addNewToDoItem("Clean the car");
        addNewToDoItem("Clean the house");
        addNewToDoItem("Buy Ketchup");
        getItemCheckbox("Buy Ketchup").click();

        assertLeftItems(2);
    }

    // CSV Source without file
    @ParameterizedTest
    @CsvSource(value = {"Backbone.js,Clean the car,Clean the house,Buy Ketchup,Buy Ketchup,2",
            "AngularJS,Clean the car,Clean the house,Clean the house,Clean the house,2",
            "React,Clean the car,Clean the house,Clean the car,Clean the car,2"}, delimiter = ',')
    public void verifyToDoListCreatedSuccessfully_withParamsCsvSourceWithoutFile(String technology, String item1, String item2, String item3, String itemToCheck, int expectedLeftItems){
        driver.navigate().to("https://todomvc.com/");
        openTechnologyApp(technology);
        addNewToDoItem(item1);
        addNewToDoItem(item2);
        addNewToDoItem(item3);
        getItemCheckbox(itemToCheck).click();

        assertLeftItems(expectedLeftItems);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data.csv", numLinesToSkip = 1)
    public void verifyToDoListCreatedSuccessfully_withParamsCsvSourceWithFile(String technology, String item1, String item2, String item3, String itemToCheck, int expectedLeftItems){
        driver.navigate().to("https://todomvc.com/");
        openTechnologyApp(technology);
        addNewToDoItem(item1);
        addNewToDoItem(item2);
        addNewToDoItem(item3);
        getItemCheckbox(itemToCheck).click();

        assertLeftItems(expectedLeftItems);
    }

    @ParameterizedTest
    @MethodSource("provideWebTechnologies")
    public void verifyToDoListCreatedSuccessfully_withMethod(String technology){
        driver.navigate().to("https://todomvc.com/");
        openTechnologyApp(technology);
        addNewToDoItem("Clean the car");
        addNewToDoItem("Clean the house");
        addNewToDoItem("Buy Ketchup");
        getItemCheckbox("Buy Ketchup").click();

        assertLeftItems(2);
    }

    @ParameterizedTest
    @MethodSource("provideWebTechnologiesMultipleParams")
    @Severity(SeverityLevel.MINOR)
    public void verifyToDoListCreatedSuccessfully_withMethod(String technology, List<String> itemsToAdd, List<String> itemsToCheck, int expectedLeftItems){
        driver.navigate().to("https://todomvc.com/");
        openTechnologyApp(technology);
        itemsToAdd.stream().forEach(itemToAdd -> addNewToDoItem(itemToAdd));
        itemsToCheck.stream().forEach(itemToCheck -> getItemCheckbox(itemToCheck).click());

        assertLeftItems(expectedLeftItems);
    }

    @Test
    @Description("verify ToDo List Created Successfully no params description")
    @Link("https://example.org")
    @TmsLink("test-1")
    @TmsLink("test-2")
    @Severity(SeverityLevel.CRITICAL)
    public void verifyToDoListCreatedSuccessfully_noParams(){
        driver.navigate().to("https://todomvc.com/");
        openTechnologyApp("Backbone.js");
        addNewToDoItem("Clean the car");
        addNewToDoItem("Clean the house");
        addNewToDoItem("Buy Ketchup");
        getItemCheckbox("Buy Ketchup").click();

        assertLeftItems(2);
    }

    private void assertLeftItems(int expectedCount){
        var resultSpan = waitAndFindElement(By.xpath("//footer/*/span | //footer/span"));
        if (expectedCount == 1){
            var expectedText = String.format("%d item left", expectedCount);
            validateInnerTextIs(resultSpan, expectedText);
        } else {
            var expectedText = String.format("%d items left", expectedCount);
            validateInnerTextIs(resultSpan, expectedText);
        }
    }

    private void validateInnerTextIs(WebElement resultElement, String expectedText){
        webDriverWait.until(ExpectedConditions.textToBePresentInElement(resultElement, expectedText));
    }

    private WebElement getItemCheckbox(String todoItem){
        var xpathLocator = String.format("//label[text()='%s']/preceding-sibling::input", todoItem);
        return waitAndFindElement(By.xpath(xpathLocator));
    }

    private void openTechnologyApp(String technologyName){
        var technologyLink = waitAndFindElement(By.linkText(technologyName));
        technologyLink.click();
    }

    private void addNewToDoItem(String todoItem){
        var todoInput = waitAndFindElement(By.xpath("//input[@placeholder='What needs to be done?']"));
        todoInput.sendKeys(todoItem);
        actions.click(todoInput).sendKeys(Keys.ENTER).perform();
    }

    private WebElement waitAndFindElement(By locator){
        return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private static Stream<String> provideWebTechnologies() {
        return Stream.of("Backbone.js",
                "AngularJS",
                "React",
                "Vue.js",
                "CanJS",
                "Ember.js",
                "KnockoutJS",
                "Marionette.js",
                "Polymer",
                "Angular 2.0",
                "Dart",
                "Elm",
                "Closure",
                "Vanilla JS",
                "jQuery",
                "cujoJS",
                "Spine",
                "Dojo",
                "Mithril",
                "Kotlin + React",
                "Firebase + AngularJS",
                "Vanilla ES6");
    }

    private Stream<Arguments> provideWebTechnologiesMultipleParams() {
        return Stream.of(
                Arguments.of("AngularJS", List.of("Buy Ketchup", "Buy House", "Buy Paper", "Buy Milk", "Buy Batteries"), List.of("Buy Ketchup", "Buy House"), 3),
                Arguments.of("React", List.of("Buy Ketchup", "Buy House", "Buy Paper", "Buy Milk", "Buy Batteries"), List.of("Buy Paper", "Buy Milk", "Buy Batteries"), 2),
                Arguments.of("Vue.js", List.of("Buy Ketchup", "Buy House", "Buy Paper", "Buy Milk", "Buy Batteries"), List.of("Buy Paper", "Buy Milk", "Buy Batteries"), 2),
                Arguments.of("Angular 2.0", List.of("Buy Ketchup", "Buy House", "Buy Paper", "Buy Milk", "Buy Batteries"), List.of(), 5)
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

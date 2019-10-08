import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.PageFactory;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.concurrent.TimeUnit;

public class SetUp {

    WebDriver driver;

    public SetUp(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public WebDriver SetDriver() {
        return SetDriver(20, 2);
    }

    public WebDriver SetDriver(int pageLoadTimeout) {
        return SetDriver(pageLoadTimeout, 2);
    }

    //@BeforeClass
    public WebDriver SetDriver(int pageLoadTimeout, int implicitlyWait) {
//        //set local property on windows PC (debug)
//        System.setProperty("webdriver.chrome.driver", "C:/... path to drivers/windows/chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--incognito");
//        WebDriver driver = new ChromeDriver(options);

        //set local property for linux remote machine
        WebDriver driver;
        WebDriverManager.chromedriver().setup();
        ChromeOptions chrome_options = new ChromeOptions();
        chrome_options.addArguments("--headless", "window-size=1366,768", "--no-sandbox");
        driver = new ChromeDriver(chrome_options);

        driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();

        driver.get(Variables.rootAppUrl);
        return driver;
    }

    //@BeforeTest
    public void SetUpBeforeTest(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    //@AfterClass
    public void TearDownAfterTest(WebDriver driver) {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
    }
}

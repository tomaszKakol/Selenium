import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public abstract class Helpers {

    public static boolean Exists(WebElement ele) {
        if (ele == null) {
            return false;
        }
        return true;
    }

    public static boolean ExistsSafely(WebElement ele) {
        try {
            if (ele == null)
                return false;
            else
                return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String RemoveSpecialChars(String word) {
        return word.replaceAll("[-+.^:,\" \"]", "");
    }

    public static String GetRandomNumber(int length) {
        Random random = new Random();
        StringBuilder digits = new StringBuilder("");

        for (int i = 1; i <= length; i++) {
            digits.append(random.nextInt(10)).toString();// [0...9]
        }

        return digits.toString();
    }

    public static String GetSpanContent(WebElement ele) {
        return ele.getAttribute("textContent").trim();
    }

    public static String GetSpanContent(WebDriver driver, By locator) {
        Helpers.WaitUntilElementIsPresent(driver, locator);
        Helpers.ScrollToElement(driver, locator);
        Helpers.WaitUntilElementIsDisplayed(driver, locator);
        return driver.findElement(locator).getAttribute("textContent").trim();
    }

    public static By GetParent(WebDriver driver, By locator) {
        By parent = (By) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].parentNode;", driver.findElement(locator));
        return parent;
    }

    public static void ClearInput(WebElement ele) {
        try {
            if (ExistsSafely(ele)) {
                ele.clear();
                ele.sendKeys(Keys.CONTROL + "a");
                ele.sendKeys(Keys.BACK_SPACE);
                ele.clear();
            }
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, ele.toString()));
            Assert.fail(String.format(Variables.errorinfo_method, "ClearInput", ex.getMessage(), ex.getLocalizedMessage(), ex));
        }
    }

    public static void ClearInput(WebDriver driver, By locator) {
        try {
            WebElement ele = driver.findElement(locator);
            if (ExistsSafely(driver.findElement(locator))) {
                ele.clear();
                ele.sendKeys(Keys.CONTROL + "a");
                ele.sendKeys(Keys.BACK_SPACE);
                ele.clear();
            }
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, locator.toString()));
            Assert.fail(String.format(Variables.errorinfo_method, "ClearInput", ex.getMessage(), ex.getLocalizedMessage(), ex));
        }
    }

    public static void SlowSendKeysWithoutClear(WebDriver driver, By locator, String word) {
        SlowSendKeysWithoutClear(driver, locator, word, 50);
    }

    public static void SlowSendKeysWithoutClear(WebDriver driver, By locator, String word, int sleepInMillis) {
        try {
            for (char ch : word.toCharArray()) {
                Thread.sleep(sleepInMillis);
                driver.findElement(locator).sendKeys(String.valueOf(ch));
                Thread.sleep(sleepInMillis);
            }
        } catch (InterruptedException ie) {
        }
    }

    public static void SlowSendKeys(WebDriver driver, By locator, String word) {
        SlowSendKeys(driver, locator, word, 100);
    }

    public static void SlowSendKeys(WebDriver driver, By locator, String word, int sleepInMillis) {
        try {
            ClearInput(driver, locator);
            WebElement ele = driver.findElement(locator);
            for (char ch : word.toCharArray()) {
                Thread.sleep(sleepInMillis);
                ele.sendKeys(String.valueOf(ch));
                Thread.sleep(sleepInMillis);
            }
        } catch (InterruptedException ie) {
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, locator.toString()));
            Assert.fail(String.format(Variables.errorinfo_method, "SlowSendKeys", ex.getMessage(), ex.getLocalizedMessage(), ex));
        }
    }

    public static void SlowSendKeys(WebElement ele, String word) {
        SlowSendKeys(ele, word, 100);
    }

    public static void SlowSendKeys(WebElement ele, String word, int sleepInMillis) {
        try {
            ClearInput(ele);
            for (char ch : word.toCharArray()) {
                Thread.sleep(sleepInMillis);
                ele.sendKeys(String.valueOf(ch));
                Thread.sleep(sleepInMillis);
            }
        } catch (InterruptedException ie) {
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, ele.toString()));
            Assert.fail(String.format(Variables.errorinfo_method, "SlowSendKeys", ex.getMessage(), ex.getLocalizedMessage(), ex));
        }
    }

    public static void ScrollToElement(WebDriver driver, WebElement ele) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ele);
    }

    public static void ClickElement(WebDriver driver, WebElement ele) {
        try {
            ScrollToElement(driver, ele);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, ele.toString()));
            Assert.fail(String.format(Variables.errorinfo_method, "ClickElement", ex.getMessage(), ex.getLocalizedMessage(), ex));
        }
    }

    public static void ScrollToElement(WebDriver driver, By locator) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(locator));
    }

    public static void Click(WebDriver driver, By locator) {
        Click(driver, locator, 10);
    }

    public static void Click(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            Helpers.WaitUntilElementIsDisplayed(driver, locator, timeoutInSeconds);
            Helpers.WaitUntilElementIsClickable(driver, locator, timeoutInSeconds);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(locator));
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, locator.toString()));
            Assert.fail(String.format(Variables.errorinfo_method, "Click", ex.getMessage(), ex.getLocalizedMessage(), ex));
        }
    }

    public static void ClickSafely(WebDriver driver, By locator) {
        ClickSafely(driver, locator, 10);
    }

    public static void ClickSafely(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WaitUntilElementIsPresent(driver, locator, timeoutInSeconds);
            Helpers.ScrollToElement(driver, locator);
            if (Helpers.IsAttributePresent(driver, locator, Variables.DISABLED)) {
                Assert.fail("Button was disabled.");
            }
            Click(driver, locator, timeoutInSeconds);
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, locator.toString()));
            Assert.fail(String.format(Variables.errorinfo_method, "ClickSafely", ex.getMessage(), ex.getLocalizedMessage(), ex));
        }
    }

    public static void MeasurePerformanceTimings(WebDriver driver) {
        try {
            System.out.println(String.format("Performance Timings for '%s' URL", driver.getCurrentUrl()));

            long loadEventEnd = (long) ((JavascriptExecutor) driver).executeScript("return window.performance.timing.loadEventEnd");
            long navigationStart = (long) ((JavascriptExecutor) driver).executeScript("return window.performance.timing.navigationStart");
            long responseStart = (long) ((JavascriptExecutor) driver).executeScript("return window.performance.timing.responseStart");
            long domComplete = (long) ((JavascriptExecutor) driver).executeScript("return window.performance.timing.domComplete");

            long backendPerformance = responseStart - navigationStart;
            long frontendPerformance = domComplete - responseStart;
            long pagePerformance = loadEventEnd - navigationStart;

            System.out.println(String.format("Back End: %d miliseconds; %d seconds", backendPerformance, TimeUnit.MILLISECONDS.toSeconds(backendPerformance)));
            System.out.println(String.format("Front End: %d miliseconds; %d seconds", frontendPerformance, TimeUnit.MILLISECONDS.toSeconds(frontendPerformance)));
            System.out.println(String.format("Total Page load time: %d milliseconds; %d seconds", pagePerformance, TimeUnit.MILLISECONDS.toSeconds(pagePerformance)));
        } catch (Exception ex) {
            Assert.fail(String.format(Variables.errorinfo_method, "MeasurePerformanceTimings", ex.getMessage(), ex.getLocalizedMessage(), ex));
        }
    }

    public static boolean IsAttributePresent(WebElement ele, String attribute) {
        try {
            String value = ele.getAttribute(attribute);
            if (value != null)
                return true;
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, ele.toString()));
            System.out.println("The element does not contains this attribute !!!\n");
        }
        return false;
    }

    public static boolean IsAttributePresent(WebDriver driver, By locator, String attribute) {
        try {
            String value = driver.findElement(locator).getAttribute(attribute);
            if (value != null)
                return true;
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, locator.toString()));
            System.out.println("The element does not contains this attribute !!!\n");
        }
        return false;
    }

    public static boolean IsAttributeValuePresent(WebElement ele, String attribute, String value) {
        try {
            String att = ele.getAttribute(attribute);
            if (att != null)
                if (ele.getAttribute(attribute).contains(value))
                    return true;
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, ele.toString()));
            System.out.println(String.format("The element does not contains attribute '%s' with value '%s'.\n", attribute, value));
        }
        return false;
    }

    public static boolean IsAttributeValuePresent(WebDriver driver, By locator, String attribute, String value) {
        try {
            String att = driver.findElement(locator).getAttribute(attribute);
            if (att != null)
                if (driver.findElement(locator).getAttribute(attribute).contains(value))
                    return true;
        } catch (Exception ex) {
            System.out.println(String.format(Variables.errorinfo_locator, locator.toString()));
            System.out.println(String.format("The element does not contains attribute '%s' with value '%s'.\n", attribute, value));
        }
        return false;
    }

    public static boolean OnlyEnglishCharacters(String string) {
        string = RemoveSpecialChars(string);
        return string.matches("[a-zA-Z]+");
    }

    public static boolean VariableIsTranslated(String celltext) {
        boolean isTranslated = true;
        if (celltext.contains("$"))
            isTranslated = false;
        return isTranslated;
    }

    public static boolean IsElementPresent(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            //driver.findElements(locator).size() != 0
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    //region Test synchronization with custom conditions

    public static boolean IsElementVisible(WebDriver driver, By locator) {
        return IsElementVisible(driver, locator, 10);
    }

    public static boolean IsElementVisible(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            if (timeoutInSeconds > 0) {
                WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
                wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean IsElementClickable(WebDriver driver, WebElement ele) {
        return IsElementClickable(driver, ele, 10);
    }

    public static boolean IsElementClickable(WebDriver driver, WebElement ele, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
            wait.until(ExpectedConditions.elementToBeClickable(ele));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void WaitUntilElementIsPresent(WebDriver driver, By locator) {
        WaitUntilElementIsPresent(driver, locator, 10);
    }

    public static void WaitUntilElementIsPresent(WebDriver driver, By locator, int timeoutInSeconds) {
        if (timeoutInSeconds > 0) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
                wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            } catch (Exception ex) {
                System.out.println(String.format("Locator: '%s' was not present.", locator.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilElementIsPresent", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    public static void WaitUntilElementIsClickable(WebDriver driver, By locator) {
        WaitUntilElementIsClickable(driver, locator, 10);
    }

    public static void WaitUntilElementIsClickable(WebDriver driver, By locator, int timeoutInSeconds) {
        if (timeoutInSeconds > 0) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
                wait.until(ExpectedConditions.elementToBeClickable(locator));
            } catch (Exception ex) {
                System.out.println(String.format("Locator: '%s' was not clickalbe.", locator.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilElementIsClickable", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    public static void WaitUntilElementIsDisplayed(WebDriver driver, By locator) {
        WaitUntilElementIsDisplayed(driver, locator, 10, 500);
    }

    public static void WaitUntilElementIsDisplayed(WebDriver driver, By locator, int timeoutInSeconds) {
        WaitUntilElementIsDisplayed(driver, locator, timeoutInSeconds, 500);
    }

    public static void WaitUntilElementIsDisplayed(WebDriver driver, By locator, int timeoutInSeconds, int pollingInMillis) {
        if (timeoutInSeconds > 0 && pollingInMillis > 0) {
            try {
                Helpers.WaitUntilElementIsPresent(driver, locator, timeoutInSeconds);
                Helpers.ScrollToElement(driver, locator);
                Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(java.time.Duration.ofSeconds(timeoutInSeconds))
                        .pollingEvery(Duration.ofMillis(pollingInMillis)).ignoring(NoSuchElementException.class);
                wait.until(d -> d.findElement(locator).isDisplayed());
            } catch (Exception ex) {
                System.out.println(String.format("Locator: '%s' was not displayed.", locator.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilElementIsDisplayed", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    public static void WaitUntilElementIsDisplayed(WebDriver driver, WebElement ele) {
        WaitUntilElementIsDisplayed(driver, ele, 10, 500);
    }

    public static void WaitUntilElementIsDisplayed(WebDriver driver, WebElement ele, int timeoutInSeconds) {
        WaitUntilElementIsDisplayed(driver, ele, timeoutInSeconds, 500);
    }

    public static void WaitUntilElementIsDisplayed(WebDriver driver, WebElement ele, int timeoutInSeconds, int pollingInMillis) {
        if (timeoutInSeconds > 0 && pollingInMillis > 0) {
            try {
                Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(java.time.Duration.ofSeconds(timeoutInSeconds))
                        .pollingEvery(Duration.ofMillis(pollingInMillis)).ignoring(NoSuchElementException.class);
                wait.until(d -> ele.isDisplayed());
            } catch (Exception ex) {
                System.out.println(String.format("Element: '%s' was not displayed.", ele.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilElementIsDisplayed", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    public static void WaitUntilAllElementsAreVisible(WebDriver driver, By locator) {
        WaitUntilAllElementsAreVisible(driver, locator, 10);
    }

    public static void WaitUntilAllElementsAreVisible(WebDriver driver, By locator, int timeoutInSeconds) {
        if (timeoutInSeconds > 0) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
                wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            } catch (Exception ex) {
                System.out.println(String.format("Locator: '%s'", locator.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilAllElementsAreVisible", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    public static void WaitUntilElementIsInvisible(WebDriver driver, By locator) {
        WaitUntilElementIsInvisible(driver, locator, 10, 500);
    }

    public static void WaitUntilElementIsInvisible(WebDriver driver, By locator, int timeoutInSeconds) {
        WaitUntilElementIsInvisible(driver, locator, timeoutInSeconds, 500);
    }

    public static void WaitUntilElementIsInvisible(WebDriver driver, By locator, int timeoutInSeconds, int pollingInMillis) {
        if (timeoutInSeconds > 0) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
                wait.pollingEvery(Duration.ofMillis(pollingInMillis)).ignoring(NoSuchElementException.class);
                wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            } catch (Exception ex) {
                System.out.println(String.format("Locator: '%s'", locator.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilElementIsInvisible", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    public static void WaitUntilElementAttributeIsPresent(WebDriver driver, WebElement ele, String attribute) {
        WaitUntilElementAttributeIsPresent(driver, ele, attribute, 10);
    }

    public static void WaitUntilElementAttributeIsPresent(WebDriver driver, WebElement ele, String attribute, int timeoutInSeconds) {
        if (timeoutInSeconds > 0) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
                wait.until(ExpectedConditions.attributeToBeNotEmpty(ele, attribute));
            } catch (Exception ex) {
                System.out.println(String.format("Locator: '%s'", ele.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilElementAttributeIsPresent", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    public static void WaitUntilElementAttributeValueIsPresent(WebDriver driver, By locator, String attribute, String value) {
        WaitUntilElementAttributeValueIsPresent(driver, locator, attribute, value, 10, 500);
    }

    public static void WaitUntilElementAttributeValueIsPresent(WebDriver driver, By locator, String attribute, String value, int timeoutInSeconds) {
        WaitUntilElementAttributeValueIsPresent(driver, locator, attribute, value, timeoutInSeconds, 500);
    }

    public static void WaitUntilElementAttributeValueIsPresent(WebDriver driver, By locator, String attribute, String value, int timeoutInSeconds, int pollingInMillis) {
        if (timeoutInSeconds > 0 && pollingInMillis > 0) {
            try {

                Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(java.time.Duration.ofSeconds(timeoutInSeconds))
                        .pollingEvery(Duration.ofMillis(pollingInMillis)).ignoring(NoSuchElementException.class);
                wait.until(d -> d.findElement(locator).getAttribute(attribute).toLowerCase().contains(value.toLowerCase()));

            } catch (Exception ex) {
                System.out.println(String.format(Variables.errorinfo_locator, locator.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilElementAttributeValueIsPresent", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    public static void WaitUntilElementAttributeValueIsNotPresent(WebDriver driver, By locator, String attribute, String value) {
        WaitUntilElementAttributeValueIsNotPresent(driver, locator, attribute, value, 10, 500);
    }

    public static void WaitUntilElementAttributeValueIsNotPresent(WebDriver driver, By locator, String attribute, String value, int timeoutInSeconds) {
        WaitUntilElementAttributeValueIsNotPresent(driver, locator, attribute, value, timeoutInSeconds, 500);
    }

    public static void WaitUntilElementAttributeValueIsNotPresent(WebDriver driver, By locator, String attribute, String value, int timeoutInSeconds, int pollingInMillis) {
        if (timeoutInSeconds > 0 && pollingInMillis > 0) {
            try {
                if (Helpers.IsElementPresent(driver, locator)) {
                Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(java.time.Duration.ofSeconds(timeoutInSeconds))
                        .pollingEvery(Duration.ofMillis(pollingInMillis)).ignoring(NoSuchElementException.class);
                wait.until(d -> d.findElement(locator).getAttribute(attribute).toLowerCase() != value.toLowerCase());
                }
            } catch (Exception ex) {
                System.out.println(String.format(Variables.errorinfo_locator, locator.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilElementAttributeValueIsNotPresent", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    public static void WaitUntilElementAttributeValueIsPresent(WebDriver driver, WebElement ele, String attribute, String value) {
        WaitUntilElementAttributeValueIsPresent(driver, ele, attribute, value, 10, 500);
    }

    public static void WaitUntilElementAttributeValueIsPresent(WebDriver driver, WebElement ele, String attribute, String value, int timeoutInSeconds) {
        WaitUntilElementAttributeValueIsPresent(driver, ele, attribute, value, timeoutInSeconds, 500);
    }

    public static void WaitUntilElementAttributeValueIsPresent(WebDriver driver, WebElement ele, String attribute, String value, int timeoutInSeconds, int pollingInMillis) {
        if (timeoutInSeconds > 0 && pollingInMillis > 0) {
            try {

                Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(java.time.Duration.ofSeconds(timeoutInSeconds))
                        .pollingEvery(Duration.ofMillis(pollingInMillis)).ignoring(NoSuchElementException.class);
                wait.until(d -> ele.getAttribute(attribute).toLowerCase().contains(value.toLowerCase()));

            } catch (Exception ex) {
                System.out.println(String.format(Variables.errorinfo_locator, ele.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilElementAttributeValueIsPresent", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    public static void WaitUntilElementAttributeValueIsNotPresent(WebDriver driver, WebElement ele, String attribute, String value) {
        WaitUntilElementAttributeValueIsNotPresent(driver, ele, attribute, value, 10, 500);
    }

    public static void WaitUntilElementAttributeValueIsNotPresent(WebDriver driver, WebElement ele, String attribute, String value, int timeoutInSeconds) {
        WaitUntilElementAttributeValueIsNotPresent(driver, ele, attribute, value, timeoutInSeconds, 500);
    }

    public static void WaitUntilElementAttributeValueIsNotPresent(WebDriver driver, WebElement ele, String attribute, String value, int timeoutInSeconds, int pollingInMillis) {
        if (timeoutInSeconds > 0 && pollingInMillis > 0) {
            try {

                Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(java.time.Duration.ofSeconds(timeoutInSeconds))
                        .pollingEvery(Duration.ofMillis(pollingInMillis)).ignoring(NoSuchElementException.class);
                wait.until(d -> ele.getAttribute(attribute).toLowerCase() != value.toLowerCase());

            } catch (Exception ex) {
                System.out.println(String.format(Variables.errorinfo_locator, ele.toString()));
                Assert.fail(String.format(Variables.errorinfo_method, "WaitUntilElementAttributeValueIsNotPresent", ex.getMessage(), ex.getLocalizedMessage(), ex));
            }
        }
    }

    //endregion

}

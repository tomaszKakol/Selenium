using System;
using System.Text;
using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Text.RegularExpressions;
using NUnit.Framework;
using OpenQA.Selenium.Interactions;

namespace ClientPortal.PageObjects
{
    [System.AttributeUsage(System.AttributeTargets.Class)]
    public class Display : System.Attribute
    {
        private string _name;

        public Display(string name)
        {
            _name = name;
        }

        public string GetName()
        {
            return _name;
        }
    }

    public static class Helpers
    {
        public static string RemoveSpecialChars(string input)
        {
            return Regex.Replace(input, @"[^0-9a-zA-Z\._]", string.Empty);
        }

        public static IWebElement FindElement(IWebDriver driver, IWebElement elem, int timeoutInSeconds = 60)
        {
            try
            {
                By by = By.CssSelector(Helpers.GetElementCssSelector(driver, elem));
                if (timeoutInSeconds > 0)
                {
                    var wait = new WebDriverWait(driver, TimeSpan.FromSeconds(timeoutInSeconds));
                    return wait.Until(drv => drv.FindElement(by));
                }
                return driver.FindElement(by);
            }
            catch(Exception)
            {
                Assert.Fail("Timeout for findElement: " + elem.TagName);
                return null;
            }
        }

        public static IWebElement FindElementSafely(IWebDriver driver, By element)
        {
            try
            {
                return driver.FindElement(element);
            }
            catch (NoSuchElementException)
            {
                return null;
            }
        }

        public static bool Exists(IWebElement element)
        {
            if (element == null)
            { return false; }
            return true;
        }

        public static bool Exists_safely(IWebElement element)
        {
            try
            {
                if (element == null)
                    return false;
                else
                    return true;
            }
            catch (NoSuchElementException)
            {
                return false;
            }
        }

        public static bool IsElementPresent(IWebDriver driver, By element)
        {
            try
            {
                driver.FindElement(element);
                return true;
            }
            catch (NoSuchElementException)
            { return false; }
        }

        public static bool IsElementDisplayed(IWebDriver driver, By locator)
        {
            try
            {
                IReadOnlyCollection<IWebElement> elements = driver.FindElements(locator);
                if (elements.Count > 0)
                { return elements.ElementAt(0).Displayed; }
                return false;
            }
            catch (NoSuchElementException)
            { return false; }
        }

        public static bool IsElementEnabled(this IWebDriver driver, By element)
        {
            try
            {
                IReadOnlyCollection<IWebElement> elements = driver.FindElements(element);
                if (elements.Count > 0)
                { return elements.ElementAt(0).Enabled; }
                return false;
            }
            catch (NoSuchElementException)
            { return false; }
        }

        public static string GetSpanContent(IWebDriver driver, IWebElement element)
        {
            return element.GetAttribute("textContent").Trim();
        }

        public static bool IsAttribtuePresent(IWebElement element, String attribute)
        {
            Boolean result = false;
            try
            {
                String value = element.GetAttribute(attribute);
                if (value != null)
                    result = true;
            }
            catch (StaleElementReferenceException ex)
            {
                Console.WriteLine("The element is no longer attached to the DOM : {0}", ex.Message);
            }
            return result;
        }

        public static void WaitForPageToLoad(IWebDriver driver)
        {
            TimeSpan timeout = new TimeSpan(0, 0, 30);
            WebDriverWait wait = new WebDriverWait(driver, timeout);

            IJavaScriptExecutor javascript = driver as IJavaScriptExecutor;
            if (javascript == null)
                throw new ArgumentException("Driver must support javascript execution");

            wait.Until((d) =>
            {
                try
                {
                    string readyState = javascript.ExecuteScript(
                        "if (document.readyState) return document.readyState;").ToString();
                    return readyState.ToLower() == "complete";
                }
                catch (InvalidOperationException e)
                {
                    //Window is no longer available
                    return e.Message.ToLower().Contains("unable to get browser");
                }
                catch (WebDriverException e)
                {
                    //Browser is no longer available
                    return e.Message.ToLower().Contains("unable to connect");
                }
                catch (Exception)
                {
                    return false;
                }
            });
        }

        private static readonly String JS_BUILD_CSS_SELECTOR =
       "for(var e=arguments[0],n=[],i=function(e,n){if(!e||!n)return 0;f" +
       "or(var i=0,a=e.length;a>i;i++)if(-1==n.indexOf(e[i]))return 0;re" +
       "turn 1};e&&1==e.nodeType&&'HTML'!=e.nodeName;e=e.parentNode){if(" +
       "e.id){n.unshift('#'+e.id);break}for(var a=1,r=1,o=e.localName,l=" +
       "e.className&&e.className.trim().split(/[\\s,]+/g),t=e.previousSi" +
       "bling;t;t=t.previousSibling)10!=t.nodeType&&t.nodeName==e.nodeNa" +
       "me&&(i(l,t.className)&&(l=null),r=0,++a);for(var t=e.nextSibling" +
       ";t;t=t.nextSibling)t.nodeName==e.nodeName&&(i(l,t.className)&&(l" +
       "=null),r=0);n.unshift(r?o:o+(l?'.'+l.join('.'):':nth-child('+a+'" +
       ")'))}return n.join(' > ');";

        public static string GetElementCssSelector(IWebDriver driver, IWebElement element)
        {

            try
            {
                IJavaScriptExecutor js = driver as IJavaScriptExecutor;

                return (String)js.ExecuteScript(JS_BUILD_CSS_SELECTOR, element);
            }
            catch (Exception ex)
            {
                Assert.Fail("Get selctor from elemement: \n" + element.TagName + "\n was failed. Test fail due to: {0}", ex);//element.GetAttribute("outerHTML")
                return null;
            }

        }

        //best-click
        public static void WaitAndClickElement(IWebDriver driver, IWebElement element)
        {
            try
            {
                Helpers.WaitForLocatorLoad(driver, By.CssSelector(Helpers.GetElementCssSelector(driver, element)));
                Helpers.FindElement(driver, element);
                new Actions(driver).MoveToElement(element).Build().Perform();
                Helpers.ScrollToElement(driver, element);
                Helpers.ClickElementSafly(driver, element);
            }
            catch (Exception ex)
            {
                Assert.Fail("Helpers method 'WaitAndClickElement' was failed.\nElement:\n"+ Helpers.GetElementCssSelector(driver, element) + "\nTest fail due to: {0}, {1}, {2}", ex.Message, ex.InnerException, ex);
            }
        }

        /// <summary>
        /// Wait until the item is Visible
        /// 
        public static void WaitForLocatorLoad(IWebDriver driver, By locator, int timeoutInSeconds = 60)
        {
            if (timeoutInSeconds > 0)
            {
                try
                {
                   // Helpers.FindElement(driver, locator, timeoutInSeconds);
                    new WebDriverWait(driver, TimeSpan.FromSeconds(timeoutInSeconds)).Until(ExpectedConditions.ElementIsVisible(locator));

                    //isElementPresent:
                    //WebDriverWait wait = new WebDriverWait(driver, waitTime);
                    //wait.until(ExpectedConditions.presenceOfElementLocated(locator));

                    //isElementClickable
                    //WebDriverWait wait = new WebDriverWait(driver, waitTime);
                    //wait.until(ExpectedConditions.elementToBeClickable(locator));

                    //isElementVisible
                    //WebDriverWait wait = new WebDriverWait(driver, waitTime);
                    //wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                    //or
                    //WebDriverWait wait = new WebDriverWait(driver, waitTime);
                    //wait.until(ExpectedConditions.visibilityOf(element));

                    //or all elements are visible
                    //List<WebElement> linkElements = driver.findelements(By.cssSelector('#linkhello'));
                    //WebDriverWait wait = new WebDriverWait(driver, waitTime);
                    //wait.until(ExpectedConditions.visibilityOfAllElements(linkElements));

                    //isElementInVisible
                    //WebDriverWait wait = new WebDriverWait(driver, waitTime);
                    //wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));

                    //isElementEnabled
                    //WebElement element = driver.findElement(By.id(""));
                    //element.isEnabled();

                    //isElementDisplayed
                    //WebElement element = driver.findElement(By.id(""));
                    //element.isDisplayed();

                    //Wait for invisibility of element
                    //WebDriverWait wait = new WebDriverWait(driver, waitTime);
                    //wait.until(ExpectedConditions.invisibilityOfElementWithText(by));

                    //Wait for invisibility of element with Text
                    //WebDriverWait wait = new WebDriverWait(driver, waitTime);
                    //wait.until(ExpectedConditions.invisibilityOfElementWithText(by, strText));
                }
                catch (Exception ex)
                {
                    Assert.Fail("Helpers method 'WaitForLocatorLoad'.\nElement: " + locator.ToString() + " does not visible.\nTest fail due to: {0}, {1}, {2}", ex.Message, ex.InnerException, ex);
                }
            }
        }

        public static IWebElement WaitUntilElementIsClickable(IWebDriver driver, By locator, int seconds = 20)
        {
            if (seconds > 0)
            {
                try
                {
                    WebDriverWait wait = new WebDriverWait(driver, TimeSpan.FromSeconds(seconds));
                    IWebElement element = wait.Until(ExpectedConditions.ElementToBeClickable(locator));
                    return element;
                }
                catch (Exception ex)
                {
                    Assert.Fail("Helpers method 'WaitUntilElementIsClickable'.\nLocator: " + locator.ToString() + " does not clickable.\nTest fail due to: {0}, {1}, {2}", ex.Message, ex.InnerException, ex);
                    return null;
                }
            }
            else
                return null;
        }

        public static bool isClickable(IWebDriver driver, IWebElement el)
        {
            try
            {
                WebDriverWait wait = new WebDriverWait(driver, TimeSpan.FromSeconds(5));
                wait.Until(ExpectedConditions.ElementToBeClickable(el));
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }

        public static void ClickElementSafly(IWebDriver driver, IWebElement element, int seconds = 15)
        {
            try
            {
                Helpers.FindElement(driver,  element);
                DefaultWait<IWebElement> wait = new DefaultWait<IWebElement>(element);
                wait.Timeout = TimeSpan.FromSeconds(10);//timeoutInSeconds
                wait.PollingInterval = TimeSpan.FromMilliseconds(200);

                Func<IWebElement, bool> waiter = new Func<IWebElement, bool>((IWebElement ele) =>
                {
                    if (Helpers.Exists_safely(ele))
                    {
                        if (ele.Enabled)
                        {
                            element.Click();
                            return true;
                        }
                    }
                    return false;
                });

                wait.Until(waiter);
                //new WebDriverWait(driver, TimeSpan.FromSeconds(seconds)).Until(ExpectedConditions.ElementToBeClickable(element)).Click();
            }
            catch (Exception ex)
            {
                Assert.Fail("Helpers method 'ClickElementSafly'.\nElemement: \n" + element.GetAttribute("outerHTML") + "\n does not clickable.\nElement CssSelector:\n'" +
                    Helpers.GetElementCssSelector(driver, element) + "'\nTest fail due to: {0}, {1}, {2}", ex.Message, ex.InnerException, ex);
            }
        }

        public static void ClickElementJS(IWebDriver driver, IWebElement element, int seconds = 15)
        {
            try
            {
                Helpers.FindElement(driver, element);
                IWebElement elementButton = driver.FindElement(By.CssSelector(Helpers.GetElementCssSelector(driver, element)));
                ((IJavaScriptExecutor)driver).ExecuteScript("arguments[0].click();", elementButton);

            }
            catch (Exception ex)
            {
                Assert.Fail("Helpers method 'ClickElementJS'.\nElemement: \n" + element.GetAttribute("outerHTML") + "\n does not clickable.\nElement CssSelector:\n'"+
                    Helpers.GetElementCssSelector(driver,element)+"'\nTest fail due to: {0}, {1}, {2}", ex.Message, ex.InnerException, ex);
            }
        }

        public static void ClickLocatorSafely(IWebDriver driver, By locator)
        {
            try
            {
                //Helpers.FindElement(driver, locator);
                Helpers.WaitForLocatorLoad(driver, locator);
                Helpers.WaitUntilElementIsClickable(driver, locator);
                IWebElement elementButton = driver.FindElement(locator);
                ((IJavaScriptExecutor)driver).ExecuteScript("arguments[0].click();", elementButton);
            }
            catch (Exception ex)
            {
                Assert.Fail("Helpers method 'ClickLocatorSafely'.\nElemement: \n" + locator.ToString() + "\n does not clickable.\nTest fail due to: {0}, {1}, {2}", ex.Message, ex.InnerException, ex);
            }
        }

        public static void WaitForInvisibilityOfElement(IWebDriver driver, By element, int timeoutInSeconds = 8)
        {
            try
            {
                if (timeoutInSeconds > 0)
                {
                    WebDriverWait wait = new WebDriverWait(driver, TimeSpan.FromSeconds(timeoutInSeconds));
                    wait.Until(ExpectedConditions.InvisibilityOfElementLocated(element));
                }
            }
            catch (Exception ex)
            {
                Assert.Fail("Action: WaitForInvisibilityOfElement, test fail due to: {0}, {1}, {2}", ex.Message, ex.InnerException, ex);
            }
        }

        public static void WaitForVisibilityOfAllElement(IWebDriver driver, By by, int timeoutInSeconds = 8)
        {
            try
            {
                if (timeoutInSeconds > 0)
                {
                    WebDriverWait wait = new WebDriverWait(driver, TimeSpan.FromSeconds(timeoutInSeconds));
                    wait.Until(ExpectedConditions.VisibilityOfAllElementsLocatedBy(by));
                }
            }
            catch (Exception ex)
            {
                Assert.Fail("Action: WaitForInvisibilityOfElement, test fail due to: {0}, {1}", ex.Message, ex.InnerException);
            }
        }


        public static bool IsAttribtuePresentFulentWait(IWebElement element, string attribute, int timeoutInSeconds = 10, int pollingInMillisec = 200)
        {
            try
            {
                DefaultWait<IWebElement> wait = new DefaultWait<IWebElement>(element);
                wait.Timeout = TimeSpan.FromSeconds(timeoutInSeconds);
                wait.PollingInterval = TimeSpan.FromMilliseconds(pollingInMillisec);

                Func<IWebElement, bool> waiter = new Func<IWebElement, bool>((IWebElement ele) =>
                {
                    if (Helpers.Exists_safely(ele))
                    {
                        if (IsAttribtuePresent(ele, attribute))
                            return true;
                    }
                    return false;
                });
                try
                {
                    wait.Until(waiter);
                }
                catch (NoSuchElementException)
                {
                    return false;
                }
                return true;
            }
            catch (Exception ex)
            {
                Assert.Fail("'Timeout'. Test fail due to: {0}, {1}", ex.Message, ex.InnerException);
                return false;
            }
        }


        public static bool IsNotAttribtuePresentFulentWait(IWebElement element, string attribute, int timeoutInSeconds = 10, int pollingInMillisec = 200)
        {
            try
            {
                DefaultWait<IWebElement> wait = new DefaultWait<IWebElement>(element);
                wait.Timeout = TimeSpan.FromSeconds(timeoutInSeconds);
                wait.PollingInterval = TimeSpan.FromMilliseconds(pollingInMillisec);

                Func<IWebElement, bool> waiter = new Func<IWebElement, bool>((IWebElement ele) =>
                {
                    if (Helpers.Exists_safely(ele))
                    {
                        if (!IsAttribtuePresent(ele, attribute))
                            return true;
                    }
                    return false;
                });
                try
                {
                    wait.Until(waiter);
                }
                catch (NoSuchElementException)
                {
                    return false;
                }
                return true;
            }
            catch (Exception ex)
            {
                Assert.Fail("'Timeout'. Test fail due to: {0}, {1}", ex.Message, ex.InnerException);
                return false;
            }
        }


        public static bool FulentWait_containsElementAttribute(IWebDriver driver, IWebElement element, string exp_attribute, string exp_value, int timeoutInSeconds = 15, int pollingInMillisec = 200)
        {
            try
            {
                string actual_value;
                DefaultWait<IWebElement> wait = new DefaultWait<IWebElement>(element);
                wait.Timeout = TimeSpan.FromSeconds(timeoutInSeconds);
                wait.PollingInterval = TimeSpan.FromMilliseconds(pollingInMillisec);

                Func<IWebElement, bool> waiter = new Func<IWebElement, bool>((IWebElement ele) =>
                {
                    if (Helpers.Exists_safely(ele))
                    {
                        if (IsAttribtuePresent(ele, exp_attribute))
                        {
                            actual_value = ele.GetAttribute(exp_attribute).ToString();
                            //Console.WriteLine("Wait, expected contains attribute: '" + exp_value + "', but was: " + actual_value);
                            if (actual_value.Contains(exp_value))
                            {
                                //Console.WriteLine("Success, the item has attribute: " + actual_value);
                                return true;
                            }
                        }
                    }
                    return false;
                });
                //try
                //{
                    wait.Until(waiter);//this suchExeception 
                //}
                //catch (NoSuchElementException)
                //{
                //    Assert.Fail("'Timeout' Locator:\n" + GetElementCssSelector(driver, element) + "\nFulentWait_containsElementAttribute");
                //    return false;
                //}
                return true;
            }
            catch (Exception ex)
            {
                Assert.Fail("'Timeout'. Locator:\n" + GetElementCssSelector(driver, element) +"\n Test fail due to: {0}, {1}", ex.Message, ex.InnerException);
                return false;
            }
        }

        public static bool FulentWait_notContainsElementAttribute(IWebDriver driver, IWebElement element, string exp_attribute, string exp_value, int timeoutInSeconds = 15, int pollingInMillisec = 200)
        {
            try
            {
                //bool isExpected = false;
                string actual_value;
                DefaultWait<IWebElement> wait = new DefaultWait<IWebElement>(element);
                wait.Timeout = TimeSpan.FromSeconds(timeoutInSeconds);
                wait.PollingInterval = TimeSpan.FromMilliseconds(pollingInMillisec);

                Func<IWebElement, bool> waiter = new Func<IWebElement, bool>((IWebElement ele) =>
                {
                    if (Helpers.Exists_safely(ele))
                    {
                        if (IsAttribtuePresent(ele, exp_attribute))
                        {
                            actual_value = ele.GetAttribute(exp_attribute).ToString();
                            //Console.WriteLine("Wait, expected no contains attribute: '" + exp_value + "', but was: " + actual_value);
                            if (!actual_value.Contains(exp_value))
                            {
                                //Console.WriteLine("Success, the item has no attribute: " + exp_attribute);
                                //isExpected = true;
                                return true;
                            }
                        }
                    }
                    return false;
                });
               //try
               //{
               wait.Until(waiter);//this suchExeception 
               //}
               //catch (NoSuchElementException)
               //{
               //Assert.Fail("'Timeout' Locator:\n"+ GetElementCssSelector(driver, element) + "\nFulentWait_notContainsElementAttribute");
               //return false;
              //}
                return true;
            }
            catch (Exception ex)
            {
                Assert.Fail("'Timeout' Locator:\n" + GetElementCssSelector(driver, element) + "\nTest fail due to: {0}, {1}, {2}", ex.Message, ex.InnerException, ex);
                return false;
            }
        }

        public static String GetRandomDigits(int length)
        {
            Random random = new Random();
            StringBuilder digits = new StringBuilder("");

            for (int i = 1; i <= length; i++)
            {
                digits.Append(random.Next(0, 9).ToString());
            }

            return digits.ToString();
        }

        public static void ClearInput(IWebElement webElement)
        {
            webElement.Clear();
            webElement.SendKeys(Keys.Control + "a");
            webElement.SendKeys(Keys.Backspace);
            webElement.Clear();
        }

        public static void SlowSendKeys(IWebElement element, string word)
        {
            ClearInput(element);
            //element.Clear();
            foreach (var letter in word)
            {
                Thread.Sleep(3);
                element.SendKeys(letter.ToString());
                Thread.Sleep(3);
            }
        }

        public static void SlowSendKeys_emptyField(IWebElement element, string word)
        {
            foreach (var letter in word)
            {
                Thread.Sleep(3);
                element.SendKeys(letter.ToString());
                Thread.Sleep(3);
            }
        }

        public static void ScrollToElement(IWebDriver driver, IWebElement element)
        {
            IWebElement elem = driver.FindElement(By.CssSelector(Helpers.GetElementCssSelector(driver, element)));
            ((IJavaScriptExecutor)driver).ExecuteScript("arguments[0].scrollIntoView(true);", elem);
        }

        public static string GetURL(IWebDriver driver)
        {
            var uri = new Uri(driver.Url);
            return uri.PathAndQuery;
        }

        public static void ScrollDown(IWebDriver driver)
        {
            IJavaScriptExecutor jse = (IJavaScriptExecutor)driver;
            jse.ExecuteScript("window.scrollBy(0,250);", "");
        }


        public static void ScrollPage(IWebDriver driver, int value)
        {
            IJavaScriptExecutor jse = (IJavaScriptExecutor)driver;
            jse.ExecuteScript("window.scrollBy(0," + value + ")", "");
        }

        public static void MeasurePerformanceTimings(IWebDriver driver)
        {
            Console.WriteLine("Performance Timings for '{0}' URL", driver.Url);

            long loadEventEnd = (long)((IJavaScriptExecutor)driver).ExecuteScript("return window.performance.timing.loadEventEnd");
            long navigationStart = (long)((IJavaScriptExecutor)driver).ExecuteScript("return window.performance.timing.navigationStart");
            long responseStart = (long)((IJavaScriptExecutor)driver).ExecuteScript("return window.performance.timing.responseStart");
            long domComplete = (long)((IJavaScriptExecutor)driver).ExecuteScript("return window.performance.timing.domComplete");

            long backendPerformance = responseStart - navigationStart;
            long frontendPerformance = domComplete - responseStart;
            long pagePerformance = loadEventEnd - navigationStart;

            Console.WriteLine("Back End: {0} miliseconds; {1} seconds", backendPerformance, TimeSpan.FromMilliseconds(backendPerformance).Seconds);
            Console.WriteLine("Front End: {0} miliseconds; {1} seconds", frontendPerformance, TimeSpan.FromMilliseconds(frontendPerformance).Seconds);
            Console.WriteLine("Total Page load time: {0} milliseconds; {1} seconds", pagePerformance, TimeSpan.FromMilliseconds(pagePerformance).Seconds);
        }
    }
}

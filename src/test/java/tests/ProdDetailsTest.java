package tests;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import pageobjects.HomePage;
import pageobjects.ProductPage;
import resources.Base;
import utilities.ExcelUtils;
import utilities.FilePaths;

public class ProdDetailsTest extends Base {
   public WebDriver driver;
    private static final Logger logger = LogManager.getLogger(ProdDetailsTest.class);

    @Test
    public void ProductDetails() throws IOException {
        String excelFilePath = FilePaths.EXCEL_FILE_PATH;
        String sheetName = "CommonCartTests";
        
        List<Map<String, String>> testCases = ExcelUtils.getTestCases(excelFilePath, sheetName);
        
        for (Map<String, String> testCase : testCases) {
            String testCaseName = testCase.get("TestCaseName");
            String executionRequired = testCase.get("Execution Required");
            String searchTerm = testCase.get("SearchTerm");
            
            if (executionRequired.equalsIgnoreCase("Yes")) {
                logger.info("Executing test case: " + testCaseName);
                logger.info("Search Term: " + searchTerm);
                
                try {
                    driver = initializeBrowser();
                    driver.get(prop.getProperty("url"));
                    logger.debug("Navigated to URL: " + prop.getProperty("url"));

                    HomePage homepage = new HomePage(driver);
                    ProductPage product = new ProductPage(driver);

                    homepage.Searchbar().sendKeys(searchTerm);
                    logger.debug("Entered search term: " + searchTerm);
                    homepage.Search().click();
                    product.ProductCard().click();

                    // Handle window switching
                    String originalWindow = driver.getWindowHandle();
                    Set<String> allWindows = driver.getWindowHandles();

                    for (String windowHandle : allWindows) {
                        if (!windowHandle.equals(originalWindow)) {
                            driver.switchTo().window(windowHandle);
                            break;
                        }
                    }

                    // Verify product details are displayed in the new window
                    Assert.assertTrue(product.Details().isDisplayed());
                    logger.info("Product details are displayed.");

                    driver.switchTo().window(originalWindow);
                } catch (Exception e) {
                    logger.error("Error occurred during the test case execution: " + testCaseName, e);
                } finally {
                    if (driver != null) {
                        driver.quit();
                        logger.debug("Browser closed for test case: " + testCaseName);
                    }
                }
            }
        }
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
            logger.debug("Browser closed in teardown method.");
        }
    }
}

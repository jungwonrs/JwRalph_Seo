package com.example.tests;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

public class VDriver {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  private static String CHROMEDRIVER_FILE_PATH;

  @Before
  public void setUp() throws Exception {

	CHROMEDRIVER_FILE_PATH = "C:/chromedriver.exe";
	System.setProperty("webdriver.chrome.driver", CHROMEDRIVER_FILE_PATH);  
	  
	driver = new ChromeDriver();
    baseUrl = "http://gplx.gov.vn/default.aspx";
    driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
  }

  @Test
  public void testVDriver() throws Exception {
    driver.get(baseUrl + "/default.aspx");
    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txt564858f0_935d_48eb_8bc4_cbb01d95fac9")).clear();
    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txt564858f0_935d_48eb_8bc4_cbb01d95fac9")).sendKeys("010061000969");
    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txtf8f10b44_097e_4198_9525_50ec5f96803f")).clear();
    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txtf8f10b44_097e_4198_9525_50ec5f96803f")).sendKeys("AA244703");
    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_cmdTraCuu")).click();
    
    
    try {
    if (driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_fb457264_9f71_4852_9e09_2721b6d15497")).isDisplayed()){
    	driver.get("http://www.daum.net");
    	Thread.sleep(2000);
    	System.out.println("ture!");
    }   
    }
    catch(Exception e){
    	driver.get("http://www.naver.com");
    	Thread.sleep(2000);
    	System.out.println("false!");
    }
    
    }
   
  @After
  public void tearDown() throws Exception {
		      
    driver.close();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}

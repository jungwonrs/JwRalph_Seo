package com.example.tests;

import java.util.concurrent.TimeUnit;
import org.junit.*;

import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
@RunWith(Parameterized.class)
public class test {

	
	  private WebDriver driver;
	  private String baseUrl;
	  private StringBuffer verificationErrors = new StringBuffer();
	  private static String CHROMEDRIVER_FILE_PATH;
	  private Request model;
	  private String DLNum;
	  private String SNum;
	  
	
	  @Parameters 
	  public static Collection getTestParameters() {
		  return Arrays.asList(new String[][]{
			  {"010061000969", "AA244703"},
			  {"000000000000", "AAA24332"},
			  {"001234030101", "AAA22345"},
			  {"010061000969", "AA244703"}
		  });
	  }
	  
	  public test(String DLNum, String SNum){
		  this.DLNum = DLNum;
		  this.SNum = SNum;
	  }
	  
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
	    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txt564858f0_935d_48eb_8bc4_cbb01d95fac9")).sendKeys(DLNum);
	    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txtf8f10b44_097e_4198_9525_50ec5f96803f")).clear();
	    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txtf8f10b44_097e_4198_9525_50ec5f96803f")).sendKeys(SNum);
	    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_cmdTraCuu")).click();
	    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_lbMessage"));
	 
	   // String text = driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_fb457264_9f71_4852_9e09_2721b6d15497")).getText();
	    
	    try {
	    if (driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_fb457264_9f71_4852_9e09_2721b6d15497")).isDisplayed()){
	    	System.out.println("ture!"+"\n");
	    	
	    	
	    WebElement tableElement = driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_fb457264_9f71_4852_9e09_2721b6d15497"));
	 	
	      List<WebElement> tableColumn = tableElement.findElements(By.tagName("td"));
	    
	    String licenseNum = tableColumn.get(0).getText();
	    String name = tableColumn.get(1).getText();
	    String dClass = tableColumn.get(2).getText();
	    String issueDate = tableColumn.get(3).getText();   
	    String expireDate = tableColumn.get(4).getText();
	    String examPass = tableColumn.get(5).getText();
	    String sNum = tableColumn.get(6).getText();
	    
	    
	    System.out.println(
	    		"Driving License Number : " + licenseNum +"\n"
	    		+"Name : " + name + "\n"
	    		+"Class : " + dClass + "\n"
	    		+"Issue Date : " + issueDate + "\n"
	    		+"Expire Date : " + expireDate + "\n"
	    		+"Date of passing exam : " + examPass + "\n"
	    		+"Serial Number : " + sNum + "\n"
	    		
	    		);
	    
	    }
	    }
	    catch(Exception e){
	    	
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

	  
	}



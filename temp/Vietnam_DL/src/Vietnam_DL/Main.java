package Vietnam_DL;
import com.sun.media.sound.ModelAbstractChannelMixer;
import java.util.concurrent.TimeUnit;
import org.junit.*;

import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

public class Main {
	private static RequestModel model;
	
	private WebDriver driver;
	private String baseUrl;
	private StringBuffer verificationErrors = new StringBuffer();
	private static String FIREFOXDRIVER_FILE_PATH;
	private String DLNum;
	private String SNum;
		
	public Main(RequestModel model){
		this.model = model;
	}
	  public void testVDriver() throws Exception {
		
		  	FIREFOXDRIVER_FILE_PATH = "C:/geckodriver.exe";
			
			System.setProperty("webdriver.gecko.driver", FIREFOXDRIVER_FILE_PATH);
			driver = new FirefoxDriver();		
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
			baseUrl = "http://gplx.gov.vn";		
		    driver.get(baseUrl + "/default.aspx");
		
		    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txt564858f0_935d_48eb_8bc4_cbb01d95fac9")).clear();
		    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txt564858f0_935d_48eb_8bc4_cbb01d95fac9")).sendKeys(model.getDNum());
		    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txtf8f10b44_097e_4198_9525_50ec5f96803f")).clear();
		    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_txtf8f10b44_097e_4198_9525_50ec5f96803f")).sendKeys(model.getSNum());
		    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_cmdTraCuu")).click();
		    driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_lbMessage"));
            
		    try {
		    if (driver.findElement(By.id("ctl00_m_g_7a640385_0766_4af4_bc09_89ae9e79308a_ctl00_fb457264_9f71_4852_9e09_2721b6d15497")).isDisplayed()){
		    	System.out.println("true"+"\n");
		    	
		   
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
		    		"Driving License Number: " + licenseNum +"\n"
		    		+"Name: " + name + "\n"
		    		+"Class: " + dClass + "\n"
		    		+"Issue Date: " + issueDate + "\n"
		    		+"Expire Date: " + expireDate + "\n"
		    		+"Date of passing exam: " + examPass + "\n"
		    		+"Serial Number: " + sNum + "\n"
		    		
		    		);
		    
		    }
		    }
		    catch(Exception e){
		    	e.printStackTrace();
		    	System.out.println("false!");
		    }
		    
		    }

		  
		}


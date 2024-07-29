package testBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

public class BaseClass {

	public static WebDriver driver;
	
	public org.apache.logging.log4j.Logger logger;
	public Properties p;
	
	
	//We need add the Grouping tags in Before class too otherwise this SetUp method will not be applicable to Test methods with Grouping tags
	@BeforeClass(groups = { "Master", "Sanity", "Regression" }) //Step8 groups added

	@Parameters({"os", "browser"})
//After using the parameters se the arguments in the method
	public void setup(String os, String br) throws IOException
	{
		
		
	    FileReader file=new FileReader(".//src//test//resources//config.properties");//Use dot before that path to refer that its with in current directory

	    p=new Properties();
	    p.load(file);
	    logger=LogManager.getLogger(this.getClass());//This is fetch the the run time class which we are executing an set it to logger
	    
	    
	    if(p.getProperty("execution_env").equalsIgnoreCase("remote"))
	    {
	    	DesiredCapabilities cap = new DesiredCapabilities();
			
	    	
	    	if(os.equalsIgnoreCase("windows"))
	    	{
	    		cap.setPlatform(Platform.WIN11); //cap.setPlatform(Platform.WIN11);
	    	}
	    	
	    	else if(os.equalsIgnoreCase("mac"))
	    	{
	    		cap.setPlatform(Platform.MAC); //cap.setPlatform(Platform.MAC);
	    	}
	    	
	    	else if(os.equalsIgnoreCase("linux"))
	    	{
	    		cap.setPlatform(Platform.LINUX); //cap.setPlatform(Platform.MAC);
	    	}
	    	
	    	else 
	    	{
	    		System.out.println("No matching OS");
	    		return;
	    	}
	    	
	    	//browser
			switch(br.toLowerCase())
			{
			case "chrome": cap.setBrowserName("chrome"); break;
			case "edge": cap.setBrowserName("MicrosoftEdge"); break;
			case "firefox": cap.setBrowserName("firefox"); break;
			default: System.out.println("No matching browser"); return;
			}

			String huburl = "http://192.168.29.172:4444/wd/hub"; //Since we are executing in stand alone server provide Hub part "wd/hub"(wd represents WebDriver) in our Grid URL
	    	driver = new RemoteWebDriver(new URL(huburl), cap);

	    }
	    if(p.getProperty("execution_env").equalsIgnoreCase("local"))
	    {
		
			switch (br.toLowerCase()) {
			case "chrome":
				driver = new ChromeDriver();
				break;
			case "edge":
				driver = new EdgeDriver();
				break;
			default:
				System.out.println("No matching browser..");
				return;// after getting invalid browser then we will exit from block using return
			}
		
	    }

		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		
		driver.get(p.getProperty("appURL"));
		driver.manage().window().maximize();
	}
	
	@AfterClass
	public void tearDown()
	{
		driver.close();
	}
	

	public String randomeString()
	{
		String generatedString=RandomStringUtils.randomAlphabetic(5);
		return generatedString;
	}
	
	public String randomeNumber()
	{
		String generatedString=RandomStringUtils.randomNumeric(10);
		return generatedString;
	}
	
	public String randomeAlphaNumeric()
	{
		String str=RandomStringUtils.randomAlphabetic(3);
		String num=RandomStringUtils.randomNumeric(3);
		
		return (str+"@"+num);
	}
	
	public String captureScreen(String tname) throws IOException {

		String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
				
		TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
		File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
		
		String targetFilePath=System.getProperty("user.dir")+"\\screenshots\\" + tname + "_" + timeStamp + ".png";
		File targetFile=new File(targetFilePath);
		
//		sourceFile.renameTo(targetFile);
		
		 FileHandler.copy(sourceFile, targetFile);
			
		return targetFilePath;

	}

}

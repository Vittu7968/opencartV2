package utilities;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
//import java.net.URL;
import java.net.URL;

//Extent report 5.x...//version

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//import org.apache.commons.mail.DefaultAuthenticator;
//import org.apache.commons.mail.ImageHtmlEmail;
//import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import testBase.BaseClass;//Imported BaseClass

public class ExtentReportManager implements ITestListener {
	public ExtentSparkReporter sparkReporter;
	public ExtentReports extent;
	public ExtentTest test;

	String repName;

	public void onStart(ITestContext testContext) {
		
		/*SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		Date dt=new Date();
		String currentdatetimestamp=df.format(dt);
		*/
		
		//The above code is written in a single way
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());// time stamp
		repName = "Test-Report-" + timeStamp + ".html";
		sparkReporter = new ExtentSparkReporter(".\\reports\\" + repName);// specify location of the report

		sparkReporter.config().setDocumentTitle("opencart Automation Report"); // Title of report
		sparkReporter.config().setReportName("opencart Functional Testing"); // name of the report
		sparkReporter.config().setTheme(Theme.DARK);
		
		extent = new ExtentReports();
		extent.attachReporter(sparkReporter);
		extent.setSystemInfo("Application", "opencart");
		extent.setSystemInfo("Module", "Admin");
		extent.setSystemInfo("Sub Module", "Customers");
		extent.setSystemInfo("User Name", System.getProperty("user.name"));
		extent.setSystemInfo("Environemnt", "QA");
		
		String os = testContext.getCurrentXmlTest().getParameter("os");//Captures os from the executed xml file
		extent.setSystemInfo("Operating System", os);
		
		String browser = testContext.getCurrentXmlTest().getParameter("browser");//Captures browser from the executed xml file
		extent.setSystemInfo("Browser", browser);
		
		List<String> includedGroups = testContext.getCurrentXmlTest().getIncludedGroups();
		if(!includedGroups.isEmpty()) {
		extent.setSystemInfo("Groups", includedGroups.toString());
		}
	}

	public void onTestSuccess(ITestResult result) {
	
		test = extent.createTest(result.getTestClass().getName());//Getting the class Name
		test.assignCategory(result.getMethod().getGroups()); // to display groups in report
		test.log(Status.PASS,result.getName()+" got successfully executed");
		
	}

	public void onTestFailure(ITestResult result) {
		test = extent.createTest(result.getTestClass().getName());//Getting the class Name
		test.assignCategory(result.getMethod().getGroups());//To the same Test we are assigning the test method Groups to it
		
		test.log(Status.FAIL,result.getName()+" got failed");
		test.log(Status.INFO, result.getThrowable().getMessage());
		
		try {
			String imgPath = new BaseClass().captureScreen(result.getName());//We are creating an object of BaseClass, so make driver in BaseClass as Static
			
			test.addScreenCaptureFromPath(imgPath);//Attaching the screenshot under failed Test within the report
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void onTestSkipped(ITestResult result) {
		test = extent.createTest(result.getTestClass().getName());
		test.assignCategory(result.getMethod().getGroups());
		test.log(Status.SKIP, result.getName()+" got skipped");
		test.log(Status.INFO, result.getThrowable().getMessage());
	}

	public void onFinish(ITestContext testContext) {
		
		extent.flush();
		
		
		//Below are additional Optional code lines
		//To open report automatically
		String pathOfExtentReport = System.getProperty("user.dir")+"\\reports\\"+repName;
		File extentReport = new File(pathOfExtentReport);
		
		try {
			Desktop.getDesktop().browse(extentReport.toURI());//Open the Report automatically
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
		//Below lines of Code are use to send report through email
//		try {
//			  URL url = new  URL("file:///"+System.getProperty("user.dir")+"\\reports\\"+repName);
//		  
//		  // Create the email message 
//		  ImageHtmlEmail email = new ImageHtmlEmail();
//		  email.setDataSourceResolver(new DataSourceUrlResolver(url));
//		  email.setHostName("smtp.googlemail.com"); //for gmail only
//		  email.setSmtpPort(465);
//		  email.setAuthenticator(new DefaultAuthenticator("aksh79681@gmail.com","vittu@7968")); 
//		  email.setSSLOnConnect(true);
//		  email.setFrom("aksh79681@gmail.com"); //Sender
//		  email.setSubject("Test Results");
//		  email.setMsg("Please find Attached Report....");
//		  email.addTo("vittu79681@gmail.com"); //Receiver 
//		  email.attach(url, "extent report", "please check report..."); 
//		  email.send(); // send the email 
//		  }
//		  catch(Exception e) 
//		  { 
//			  e.printStackTrace(); 
//			  } 
		 
	}

}

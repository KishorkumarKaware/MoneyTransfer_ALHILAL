package com.vyoms.whatsapp.type;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.HashMap;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;

import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;

import org.openqa.selenium.Keys;

import org.openqa.selenium.NoSuchElementException;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.interactions.Actions;

import org.openqa.selenium.support.ui.ExpectedConditions;

import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import com.vyom.rasa.integration.validate_Response;
import com.vyom.whatsapp.rest.aduserreset.aeRestCallDoOcr;
import com.vyoms.whatsapp.implementation.WhatsAppImplementation;

import com.vyoms.whatsapp.model.AgentMaster;

import com.vyoms.whatsapp.model.EmpMaster;

import com.vyoms.whatsapp.model.PinMaster;

import com.vyoms.whatsapp.model.Policy;
import com.vyoms.whatsapp.model.VehicalDetails;
import com.vyoms.whatsapp.service.AgentMasterService;

import com.vyoms.whatsapp.service.EmpMasterService;

import com.vyoms.whatsapp.service.PinMasterService;

import com.vyoms.whatsapp.util.Constants;

import com.vyoms.whatsapp.util.DriverUtility;
import com.vyoms.whatsapp.util.ValidateMobileNumber;

import ch.qos.logback.core.net.SyslogOutputStream;
import util.downloadFilePath;

public class WhatsApp implements WhatsAppImplementation {


	public static Actions actions;

	public static int browserType;

	public static WebDriver driver = null;

	public static boolean init = false;

	public static HashMap<String, String> msgs = null;

	public static boolean inLoop = false;

	public static HashMap<String, Date> lastReply = new HashMap<>();

	public static String rasa_Response=null;

	/*

	 * public WhatsApp(PinMasterService pinService, AgentMasterService

	 * agentService, EmpMasterService empService) { //pinMasterService =

	 * pinService; //agentMasterService = agentService; //empMasterService =

	 * empService; }

	 */

	public static String downloadImage() throws InterruptedException {
		Thread.sleep(2000);
		driver.switchTo().defaultContent();
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("main")));

		WebElement main = driver.findElement(By.id("main"));
		String result = null;
		List<WebElement> msgs = main.findElements(By.className("image-thumb"));
		WebElement msg = msgs.get(msgs.size() - 1);
		try {
			wait = new WebDriverWait(driver, 15);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("spinner-container")));
			WebElement resultImg = msg.findElement(By.tagName("img"));
			resultImg.click();
			driver.switchTo().defaultContent();
			wait = new WebDriverWait(driver, 15);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("app")));
			WebElement app = driver.findElement(By.id("app"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("media-panel-header")));
			WebElement mediaPanelHeader = app.findElement(By.className("media-panel-header"));//// *[@id="app"]/div/span[2]/div/div/div[2]/div[1]
			// WebElement mediaPanelHeaderpan = (WebElement)
			// app.findElement(By.className("menu menu-horizontal media-panel-tools"));
			// WebElement downloadpanel = (WebElement)
			// mediaPanelHeader.findElement(By.tagName("menu menu-horizontal
			// media-panel-tools"));
			WebElement hover = mediaPanelHeader.findElement(By.xpath("div[2]"));
			// System.out.println(hover.size());
			WebElement downloadButton = hover.findElement(By.xpath("div[4]"));
			// System.out.println(downloadButton.size());
			try {
				downloadButton.click();
			} catch (Exception e) {
				wait.until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//*[@id='app']/div/span[2]/div/div/div[2]/div[1]/div[2]/div[4]/div")));
				driver.findElement(By.xpath("//*[@id='app']/div/span[2]/div/div/div[2]/div[1]/div[2]/div[4]/div"))
				.click();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			WebElement closeButton = mediaPanelHeader.findElement(By.tagName("button"));
			closeButton.click();
		} catch (Exception e) {
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//*[@id='app']/div/span[2]/div/div/div[2]/div[1]/div[2]/div[5]")));
				driver.findElement(By.xpath("//*[@id='app']/div/span[2]/div/div/div[2]/div[1]/div[2]/div[5]")).click();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		System.out.println("Message=" + result);
		Thread.sleep(2000);
		return result;
	}


	public long SourceIDSeq = 94;//113;

	AgentMasterService agentMasterService;

	public HashMap<String, AgentMaster> agents;

	String downloadFilepath = Constants.downloadFilepath;

	public HashMap<String, EmpMaster> employees;

	EmpMasterService empMasterService;

	public String gTickets;

	public HashMap<String, PinMaster> pinCodes;

	PinMasterService pinMasterService;

	public HashMap<String, Policy> policy;

	protected String rep;

	// Changes for Intermidate Id

	public HashMap<String, String> validIntermediateIds;
	/*

                public WhatsApp(PinMasterService pinMasterService, AgentMasterService agentMasterService,

                                                EmpMasterService empMasterService) {

                                // TODO Auto-generated constructor stub

                }*/

	public WhatsApp() {

		// TODO Auto-generated constructor stub

	}

	public void cleanBrowser() {

		String cmd = "Taskkill /IM chromedriver.exe /F";

		try {

			Process p = Runtime.getRuntime().exec(cmd);

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public String getFileName(String filePath) {

		String fileName = new StringBuffer().append(" ")

				.append(filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length())).append(" ").toString();

		return fileName.replaceAll("\\P{Print}", "").trim();

	}

	public String getMessage(String msg) {

		String message = new StringBuffer().append(" ").append(msg).append(" ").toString();

		return message.replaceAll("\\P{Print}", "").trim();

	}

	public void init(int bType) {

		if (init == false) {

			System.out.println("Startig INIT ");

			driver = DriverUtility.getDriver(bType, downloadFilepath);

			actions = new Actions(driver);

			msgs = new HashMap<String, String>();

			policy = new HashMap<>();

			browserType = bType;

		}

	}

	// Changes for Intermidate Id

	public Boolean isValidIntermediateId(String id) {

		boolean result = false;

		if (validIntermediateIds.containsKey(id))

			result = true;

		return result;

	}

	public String messageReply(String from, String msg) throws Exception {

		/*if(msg.contains(":")){
			String str1[]=msg.split(":");  
			from = str1[0];  
			msg=str1[1];
		}*/
		from = from.replace("+91", "").replace(" ", "");
		String reply = "No valid input";
		Pattern agent_Im_Id_pattrn = Pattern.compile("[a-zA-Z]{4}\\d{5}");
		// agent_Im_Id.matcher(msg);
		Matcher agent_Im_Id = agent_Im_Id_pattrn.matcher(msg);
		Pattern intermediateIdPattern = Pattern.compile("(\\d{12})");
		intermediateIdPattern.matcher(msg);
		Pattern.compile(
				"(([A-D]{1}|[a-d]{1})[1-4]{1})|(([A-D]{1}|[a-d]{1})[1-4]{1}[$]{1}[0-9]{1,7})|(([A-D]{1}|[a-d]{1})[1-4]{1}[@]{1}[0-9]{1,5})|(([A-D]{1}|[a-d]{1})[1-4]{1}[$]{1}[0-9]{1,7}[@]{1}[0-9]{1,5})|(([A-D]{1}|[a-d]{1})[1-4]{1}[@]{1}[0-9]{1,5}[$]{1}[0-9]{1,7})");
		from.split(" ");


		System.out.println("message from "+from+" is "+msg);

		try{
			/*try
            {
            	if(msg.matches((".*\\d.*")))
            	{

            		rasa_Response=msg;
            	}
            }
            catch(ConnectException e)
            {

            }*/
			if(msg.equalsIgnoreCase("i") || msg.equalsIgnoreCase("ii") || msg.equalsIgnoreCase("iii"))
			{
				rasa_Response=msg;
			}else if(msg.equalsIgnoreCase("exit"))
			{
				rasa_Response = msg;
			}else if(msg.contains("otp"))
			{
				rasa_Response =msg;
				String str[]=msg.split(" ");
				//rasa_Response =str[str.length-1];
				//policy.get(from).setOtp(str[str.length-1]);
			}else 
			{     
				rasa_Response =msg;
				//rasa_Response=(new validate_Response()).check_response(msg);
				System.out.println("Rasa default responce="+rasa_Response);
			}


		}/*catch(ConnectException e){

			reply="Hi,"+from+" Unnable to connect with RASA.";

			sendMessage(from, reply);

			policy.get(from).setStart(false);

			policy.remove(from);

			reply="Hi,"+from+" Your session has ben teminated Please ping us again \n Thank You !! ";

			sendMessage(from, reply);

			return removeSpecialCharacter(reply);

		}*/
		catch(Exception e){}

		String sheet ="";
		int bal=0;
		String name="";
		System.out.println("Rasa Responce="+rasa_Response);
		if ((!policy.containsKey(from) && (rasa_Response.equalsIgnoreCase("greet") || rasa_Response.equalsIgnoreCase("hi")) && (from.contains("9921711696")  || from.contains("8698206727") || from.contains("8007970174"))))
		{

			File file=new File("C:\\Users\\Administrator\\Desktop\\BankAcoountData.xlsx");
			FileInputStream fin=new FileInputStream(file);
			XSSFWorkbook wb=new XSSFWorkbook(fin);
			XSSFSheet ws=null;

			ws=wb.getSheetAt(0);
			XSSFCell cell=null;
			XSSFRow rowheader=null;
			rowheader=ws.getRow(0);


			String arr[]=new String[2];
			int rowNum=ws.getLastRowNum()+1;
			int colNum = ws.getLastRowNum();
			int nameindexheader=0;
			int contactnumberindexheader=1;
			int accountnumberindexheader=2;
			int balnancenumberindexheader=3;
			int otpindexheader=4;




			for(int i=1;i<=colNum;i++)
			{ 
				rowheader=ws.getRow(i);
				String contactnumber=cellToString(rowheader.getCell(contactnumberindexheader));
				if(contactnumber.equals(from))
				{
					if(!policy.containsKey(from))
					{

						final Policy saveImageForAgent = new Policy();

						saveImageForAgent.setStart(true);

						policy.put(from, saveImageForAgent);
					}
					name=cellToString(rowheader.getCell(nameindexheader));
					policy.get(from).setNameOfCustomer(name);
					System.out.println("\nName is:"+name);

					policy.get(from).setContactNumberOfCustomer(contactnumber);
					// int intime=Integer.valueOf(String)accountnumberindex);	 
					System.out.println("\nContact Number is:"+contactnumber);

					String accountnumber=cellToString(rowheader.getCell(accountnumberindexheader));
					policy.get(from).setAccountNumOfCustomer(accountnumber);
					System.out.println("\nAccount Number is:"+accountnumber);

					String balance=cellToString(rowheader.getCell(balnancenumberindexheader));
					policy.get(from).setBalance(balance);
					//int bal=Integer.valueOf(String)balnancenumberindexheader);
					bal=Integer.valueOf((String)balance);
					System.out.println("\nConverted integer balance is:"+bal);
					System.out.println("\nBalance is:"+balance);

					String otp=cellToString(rowheader.getCell( otpindexheader));
					policy.get(from).setOtp(otp);
					System.out.println("\nOTP Number is:"+otp);

					sheet = from+"_PayeeAccountData";

					System.out.println("\nsheet is:"+sheet);
					break;
				}

			}

			reply="Hello! "+ name +" \nYou have reached Al Hilal Premium Customer Bot."+"\nHow may I assist you today?";
			sendMessage(from, reply);
			msg="";
			fin.close();
			return removeSpecialCharacter(reply);
		}

		System.out.println("Rasa Responce="+rasa_Response);
		//String nickname="";
		if(rasa_Response.contains("payee") && !rasa_Response.contains("amount"))
		{

			String payee[]=rasa_Response.split(":");
			policy.get(from).setNickname(payee[payee.length-1]);

		}
		else if(rasa_Response.contains("amount") && rasa_Response.contains("payee"))
		{
			String payeeamount1[]=rasa_Response.split(" ");

			String amount1[]=payeeamount1[0].split(":");
			policy.get(from).setAmount(amount1[amount1.length - 1]);
			System.out.println("Set Amount is="+policy.get(from).getAmount());
			String payee1[]=payeeamount1[1].split(":");
			policy.get(from).setNickname(payee1[payee1.length - 1]);

		}
		else if(rasa_Response.contains("amount") && !rasa_Response.contains("payee"))
		{
			String amount2[]=rasa_Response.split(":");
			policy.get(from).setAmount(amount2[amount2.length - 1]);
			System.out.println("Set Amount is="+policy.get(from).getAmount());
		}



		if(policy.containsKey(from) && policy.get(from).isStart() && policy.get(from).getNickname()!= null && policy.get(from).getNameOfPayee()==null)
		{
			File file=new File("C:\\Users\\Administrator\\Desktop\\BankAcoountData.xlsx");
			FileInputStream fin=new FileInputStream(file);
			XSSFWorkbook wb=new XSSFWorkbook(fin);
			XSSFSheet ws= null;
			if(from.equals("8698206727")){
				ws=wb.getSheet("8698206727_PayeeAccountData");
			}else if(from.equals("9921711696"))
			{
				ws=wb.getSheet("9921711696_PayeeAccountData");
			}else if(from.equals("8007970174"))
			{
				ws=wb.getSheet("8007970174_PayeeAccountData");
			}		
			XSSFRow rowheader=ws.getRow(0);
			int rowNum=ws.getLastRowNum()+1;
			int colNum = ws.getLastRowNum();

			int payeeNameindexheader=0;
			int nickNameindexheader=1;
			int accountTypeindexheader=2;
			int Accountnumberindexheader=3;
			String nickname="";

			for(int j=1;j<=colNum;j++)
			{
				rowheader=ws.getRow(j);
				nickname=cellToString(rowheader.getCell(nickNameindexheader));
				System.out.println(nickname+"="+policy.get(from).getNickname());
				if(policy.get(from).getNickname().equalsIgnoreCase(nickname))
				{
					String payeename=cellToString(rowheader.getCell(payeeNameindexheader));
					policy.get(from).setNameOfPayee(payeename);
					System.out.println("\nPayeeName is:"+payeename);

					System.out.println("\nNick Name is:"+nickname);

					String accountype=cellToString(rowheader.getCell(accountTypeindexheader));
					policy.get(from).setAccountType(accountype);
					System.out.println("\nAccount Type is:"+accountype);

					String accountnumber1=cellToString(rowheader.getCell(Accountnumberindexheader));
					policy.get(from).setAccountNumberOfPayee(accountnumber1);
					System.out.println("\nAccount number:"+accountnumber1);
					break;
				}
				else if(j>=colNum)
				{
					reply="Sorry!! Requested Payee is not available";

					sendMessage(from, reply);
					return removeSpecialCharacter(reply);
				}
			}


			policy.get(from).setOption(rasa_Response);
			if(policy.containsKey(from) && policy.get(from).isStart() &&  policy.get(from).getNickname()!=null && policy.get(from).getAmount()!= null)
			{
				reply="Request you to reconfirm the amount to be transferred which is Rs "+ policy.get(from).getAmount() +" for smooth processing.";
				sendMessage(from, reply);
				return removeSpecialCharacter(reply);
			}
		}
		if(policy.containsKey(from) && policy.get(from).isStart() && policy.get(from).getNickname()==null){

			reply = "Great! Let me assist you with the transfer."+"\nPlease enter the name of Payee to whome you would like to transfer money?";
			sendMessage(from, reply);	
			return removeSpecialCharacter(reply);
		}

		if(policy.containsKey(from) && policy.get(from).isStart() && policy.get(from).getAmount()== null && policy.get(from).getNickname()!=null){

			reply = "Great! Let me assist you with the transfer. How much would you like to transfer?";
			sendMessage(from, reply);	
			return removeSpecialCharacter(reply);
		}
		int amountleft=0;
		String amt="";
		try{
			bal=Integer.valueOf((String)policy.get(from).getBalance());
			amt=policy.get(from).getAmount();
			System.out.println("user Entered Amount"+amt);
			amountleft=Integer.valueOf((String)amt);
			System.out.println("Amountleft:"+amountleft);
		}catch(Exception e){
			System.out.println("Amount not converted in int "+amt);
		}
		if(amountleft > bal)
		{
			reply="Sorry! Your balance is low. The available balance is "+bal;
			sendMessage(from, reply);	
		}


		//System.out.println("policy.get(from).getTransType()="+policy.get(from).getTransType());
		if(policy.containsKey(from) && policy.get(from).isStart() && rasa_Response.equalsIgnoreCase("i")){
			policy.get(from).setTransType("NEFT");
			reply=" Done! Your request is accepted. An OTP is send to your registered mobile number/email id."+"\nPlease enter your OTP for the verification.";
			sendMessage(from, reply);	

		}else if(policy.containsKey(from) && policy.get(from).isStart() && rasa_Response.equalsIgnoreCase("ii")){
			policy.get(from).setTransType("RTGS");
			reply=" Done! Your request is accepted. An OTP is send to your registered mobile number/email id."+"\nPlease enter your OTP for the verification.";
			sendMessage(from, reply);

		}else if(policy.containsKey(from) && policy.get(from).isStart() && rasa_Response.equalsIgnoreCase("iii")){
			policy.get(from).setTransType("IMPS");

			reply=" Done! Your request is accepted. An OTP is send to your registered mobile number/email id."+"\nPlease enter your OTP for the verification.";
			sendMessage(from, reply);	
		}else if(policy.containsKey(from) && policy.get(from).isStart() && policy.get(from).getTransType() == null)
		{
			reply=" You can transfer Money."+"\nPlease enter your preferred choice of transaction. (Ex. i.NEFT, ii.RTGS, iii.IMPS)";
			sendMessage(from, reply);	
		}
		if (policy.containsKey(from) && policy.get(from).isStart() && rasa_Response.contains("otp") && policy.get(from).getOtp() !=null )
		{
			String str[]=msg.split(" ");
			String t =str[str.length-1];
			System.out.println("User Entered OTP="+t);
			System.out.println("Excel sheet OTP="+policy.get(from).getOtp());
			if(policy.get(from).getOtp().equals(t))
			{
				String[] temp =policy.get(from).getAccountNumberOfPayee().split("");
				
				reply="Thank you for your transaction. You have successfully transferred "+ policy.get(from).getAmount() + " in the account number XX"+temp[temp.length-4]+""+temp[temp.length-3]+""+temp[temp.length-2]+""+temp[temp.length-1];
				sendMessage(from, reply);
				bal=Integer.valueOf((String)policy.get(from).getBalance());
				System.out.println("Excel Sheet amount="+bal +" User amount="+amountleft);
				int availablebalance=(bal-amountleft);
				reply ="Available balance is: "+availablebalance;
				sendMessage(from, reply);
				reply="Type exit to terminate";
				sendMessage(from, reply);
			}
			else
			{
				reply="Sorry! Entered OTP is invalid";
				sendMessage(from, reply);
				return removeSpecialCharacter(reply);
			}
		}
		if(policy.containsKey(from) && policy.get(from).isStart() && rasa_Response.equalsIgnoreCase("reject")){
			reply="Thank You for contacting us..Ping us if needed";
			sendMessage(from, reply);
		}
		if (policy.containsKey(from)&& policy.get(from).isStart() && msg.equalsIgnoreCase("exit"))
		{
			policy.get(from).setStart(false);
			policy.remove(from);
			reply="Thank you for reaching Al Hilal Premium Customer Bot . It was a pleasure assisting you."+"\nDo reach us for further assistance. Have a great day.";
			sendMessage(from, reply);
		}
		/*else if(reply.equalsIgnoreCase("Sorry! You have entered invalid input.") && !(from.equals("8698206727"))  || !(from.equals("9921711696"))){
			//reply="Invalid Option\n"+reply;
			//policy.get(from).setStart(false);
			//policy.remove(from);
		}*//*else
		{
			sendMessage(from, reply);
		}*/

		return removeSpecialCharacter(reply);

	}
	private String cellToString(XSSFCell cell) {
		String date ="";
		Object result = null;
		switch (cell.getCellType()) 
		{
		case XSSFCell.CELL_TYPE_NUMERIC:
			//Date date = new Date();
			if (HSSFDateUtil.isCellDateFormatted(cell))                            
			{
				SimpleDateFormat sdfdate = new SimpleDateFormat("MM-dd-yyyy");
				//SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

				date = sdfdate.format(cell.getDateCellValue());
				// System.out.println(date.toString());
				result=date.toString();
			}
			else 
			{
				Double e1Val = cell.getNumericCellValue();
				BigDecimal bd = new BigDecimal(e1Val.toString());
				long lonVal = bd.longValue();
				result = String.valueOf(lonVal);			
			}
			break;
		case XSSFCell.CELL_TYPE_STRING:

			result = cell.getStringCellValue();
			break;
		case XSSFCell.CELL_TYPE_BLANK:
			result = "";
			break;
		case XSSFCell.CELL_TYPE_FORMULA:
			result = cell.getCellFormula();
		}
		return result.toString();

	}

	private static boolean getMail(String email )
	{

		String[] str = email.split(" ");

		for(int i=0; i<str.length; i++){
			if(str[i].contains("@") || str[i].contains(".")){
				email = str[i];
			}
		}
		System.out.println("Emial Id="+email);
		boolean flag1 = true;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;
		boolean flag5 = false;
		boolean flag6 = false;
		int count = 0;

		int temp = email.length();
		if(email.contains("@")){
			flag2=true;
			int a=email.indexOf("@");
			for(int i=a;i<temp;i++){
				if(email.charAt(i)=='.'){
					flag3=true; 
					count=count+1;
				}
			}
			if(count<1||count>2){
				flag4=false;
			}
			else{
				flag4=true;
			}
		}
		else{
			flag2 =false;
			System.out.println("No @ symbol present");
			return false;
		}


		//Condition 3
		if(email.matches("[A-Z a-z 0-9_]+@.*")) //Unable to get the right RegEx here!
			flag5 = true;
		else
			flag5 = false;

		//Condition4
		if(!Character.isUpperCase(email.charAt(0))==true)
			flag6 = true;
		else
			flag6=false;

		if(flag1==true && flag2==true && flag3==true && flag4==true && flag5==true &&flag6==true){
			System.out.println("Email ID is valid");
			return true;
		}	    else{
			if(flag1==false){
				System.out.println("Inavlid length of Email ID");
				return false;
			}
			if(flag2==false||flag3==false||flag4==false){
				System.out.println("Invalid Position of Special Characters");
				return false;
			}
			if(flag5==false){
				System.out.println("Invalid combination for username");
				return false;
			}
			if(flag6==false){
				System.out.println("Invalid case of first letter");
				return false;
			}
		}
		return true;
	}

	public boolean processMessage(String key)
	{

		Date d1 = lastReply.get(key);

		if (d1 == null)

			return true;

		Date d2 = new Date();

		long seconds = (d2.getTime() - d1.getTime()) / 1000;

		if (seconds > 1) {

			return true;

		}
		return false;

	}
	public String readLastMessage() throws InterruptedException {
		Thread.sleep(2000);
		driver.switchTo().defaultContent();
		WebElement main = driver.findElement(By.id("main"));
		String result = null;
		List<WebElement> msgs = main.findElements(By.className("message-list"));
		WebElement msg = msgs.get(msgs.size() - 1);
		try {
			result = msg.findElement(By.className("message-text")).findElements(By.tagName("span")).get(1).getText();
		} catch (Exception e) {
			result = msg.findElement(By.className("message-system-e2e")).findElement(By.className("emojitext"))
					.getText();
			if (result.equals("Messages you text to this chat and calls are secured with end-to-end encryption.")) {
				msg = msgs.get(msgs.size() - 2);
				result = msg.findElement(By.className("message-text")).findElements(By.tagName("span")).get(1)
						.getText();
			}
		}
		System.out.println("Message=" + result);
		return result;
	}
	public String removeSpecialCharacter(String temp) {

		try {

			return temp.replaceAll("\\P{Print}", "").trim();

		} catch (Exception e) {

			return temp;

		}

	}

	public void sendDocument(String title, String docPath) throws InterruptedException {

		driver.switchTo().defaultContent();

		WebDriverWait wait = new WebDriverWait(driver, 30);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("main")));

		WebElement main = driver.findElement(By.id("main"));

		WebElement chatControl = main.findElement(By.className("pane-chat-controls"));

		List<WebElement> items = chatControl.findElements(By.className("menu-item"));

		WebElement attachment = items.get(1).findElement(By.tagName("button"));

		attachment.click();



		WebElement docAttachment = items.get(1).findElements(By.tagName("input")).get(1);

		docAttachment.sendKeys(docPath);



		wait = new WebDriverWait(driver, 30);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("drawer-body")));

		WebElement drawerBody = driver.findElement(By.className("drawer-body"));



		wait = new WebDriverWait(driver, 30);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("drawer-controls")));

		WebElement textButton = drawerBody.findElement(By.className("drawer-controls"))

				.findElement(By.tagName("button"));

		textButton.click();

	}

	public void sendImage(String title, String imgPath, String msg) throws InterruptedException {

		driver.switchTo().defaultContent();

		WebDriverWait wait = new WebDriverWait(driver, 15);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("main")));

		WebElement main = driver.findElement(By.id("main"));

		WebElement chatControl = main.findElement(By.className("pane-chat-controls"));

		List<WebElement> items = chatControl.findElements(By.className("menu-item"));

		WebElement attachment = items.get(1).findElement(By.tagName("button"));

		attachment.click();



		WebElement imageAttachment = items.get(1).findElements(By.tagName("input")).get(0);

		imageAttachment.sendKeys(imgPath);



		wait = new WebDriverWait(driver, 15);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("drawer-body")));

		WebElement drawerBody = driver.findElement(By.className("drawer-body"));



		wait = new WebDriverWait(driver, 15);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("input-wrapper")));



		WebElement inputWrapper = drawerBody.findElement(By.className("input-wrapper"));

		WebElement inputEmoji = drawerBody.findElement(By.className("input-emoji"));

		WebElement inputText = inputEmoji.findElement(By.tagName("div"));

		inputWrapper.click();

		inputText.sendKeys(msg);



		WebElement textButton = drawerBody.findElement(By.className("drawer-controls"))

				.findElement(By.tagName("button"));

		textButton.click();

	}

	public void sendMessage(String title, String msg) throws InterruptedException {
		Thread.sleep(1000);
		driver.switchTo().defaultContent();
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("main")));
		WebElement main = driver.findElement(By.id("main"));
		WebElement messageList = main.findElement(By.className("_9tCEa"));
		List<WebElement> messages = messageList.findElements(By.className("msg"));
		String lastMessage = null;
		if (messages.size() >= 1) {
			WebElement message = messages.get(messages.size() - 1);
			WebElement msgLast = null;
			try {
				msgLast = message.findElement(By.className("message-text"));
			} catch (ElementNotFoundException e) {
				// TODO: handle exception
			} catch (NoSuchElementException e) {
			}

			try {
				System.out.println(msgLast.getText());
				// lastMessage = msgLast.findElements(By.tagName("span")).get(1).getText();
				lastMessage = msgLast.getText();
			} catch (NullPointerException e) {
				// TODO: handle exception
			}
		}
		boolean alreadySent = false;
		if (lastMessage != null && removeSpecialCharacter(lastMessage).equals(removeSpecialCharacter(msg))) {
			alreadySent = true;
		}
		if (!alreadySent) {
			WebElement blockCompose = main.findElement(By.className("block-compose"));
			WebElement inputTextDiv = blockCompose.findElement(By.className("input-container"));
			wait = new WebDriverWait(driver, 15);
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//*[@id='main']/footer/div[1]/div[2]/div/div[2]")));
			WebElement inputText = inputTextDiv
					.findElement(By.xpath("//*[@id='main']/footer/div[1]/div[2]/div/div[2]"));
			inputText.click();
			inputText.clear();
			inputText.sendKeys(msg.replaceAll("\n", Keys.chord(Keys.SHIFT, Keys.ENTER)));
			// inputText.sendKeys(msg);please wait while we are genrating your
			// quote \n(you shall receive an email for the same )
			WebElement button = blockCompose.findElements(By.className("compose-btn-send")).get(0);
			button.click();
			System.out.println("Sent Message=" + msg);
			msgs.put(title, removeSpecialCharacter(msg));
		}

	}
	/* Download PDF */

	public boolean downloadDoc() throws InterruptedException {

		Thread.sleep(2000);

		driver.switchTo().defaultContent();

		WebDriverWait wait = new WebDriverWait(driver, 15);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("main")));



		WebElement main = driver.findElement(By.id("main"));

		boolean result = false;

		List<WebElement> msgs = main.findElements(By.className("document-body"));

		WebElement msg = msgs.get(msgs.size() - 1);

		try {

			wait = new WebDriverWait(driver, 15);

			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("spinner-container")));

			WebElement resultDoc = msg.findElement(By.className("doc-state"));

			resultDoc.click();

			result =true;
			//result = msg.findElement(By.className("message-system-e2e")).findElement(By.className("emojitext")).getText();

		} catch (NoSuchElementException e) {


		}

		//System.out.println("Message=" + result);

		Thread.sleep(3000);

		return result;

	}


	@Override
	public void userList() throws Exception {

		if (driver == null) {

			WhatsApp.driver = DriverUtility.getDriver(browserType, downloadFilepath);

			init = false;

		}

		if (init == false) {

			driver.get("https://web.whatsapp.com/");

			WebDriverWait wait = new WebDriverWait(driver, 60);

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pane-side")));

			init = true;

		}

		inLoop = false;

		if (inLoop == false) {

			inLoop = true;

			driver.switchTo().defaultContent();

			WebElement side = driver.findElement(By.id("pane-side"));

			WebElement paneSide = side.findElement(By.id("pane-side"));

			List<WebElement> chats = paneSide.findElements(By.className("chat-body"));// ("div")).findElement(By.tagName("div")).findElement(By.tagName("div")).findElements(By.tagName("div"));

			for (WebElement chat : chats) {

				//System.out.println("Active Chats Size:-" + chats.size());

				// chats = paneSide.findElements(By.className("chat-body"));

				try {

					try {

						actions.moveToElement(chat).click().perform();

						// chat.click();

					} catch (Exception e1) {

						// TODO Auto-generated catch block

						chat.click();

						actions.moveToElement(chat).click().perform();

					}

					WebElement chatTitle = chat.findElement(By.className("chat-main"))

							.findElement(By.className("chat-title")).findElement(By.tagName("span"));
					// System.out.println("chatTitleAttrib: -"+chatTitle.getAttribute("title"));
					// System.out.println("chatTitleText: -"+chatTitle.getText());
					WebElement chatSecondary = chat.findElement(By.className("chat-secondary"));// last-msg
					// System.out.println("ChatSecondaryText: -"+chatSecondary.getText());
					// System.out.println("ChatSecondaryAttrib:
					// -"+chatSecondary.getAttribute("title"));
					// System.out.println("ChatSecondary: -"+chatSecondary.getTagName());
					WebElement lastMsg = chatSecondary.findElement(By.className("chat-status"));
					// System.out.println("LastMsg get Attribute: -"+lastMsg.getText());
					// System.out.println("LastMsg get Text: -"+lastMsg.getText());
					String chatUnreadCount = chat.findElement(By.className("chat-secondary"))

							.findElement(By.className("chat-meta")).findElement(By.tagName("span")).getText();

					if (!chatUnreadCount.equals("") && !msgs.containsKey(chatTitle.getAttribute("title"))) {

						msgs.put(chatTitle.getAttribute("title"),

								removeSpecialCharacter("Old" + lastMsg.getText()));

					}

					actions.moveToElement(chat).click().perform();

					// System.out.println(chatTitle.getAttribute("title") + "="

					// + lastMsg.getAttribute("title"));

					WebDriverWait wait = new WebDriverWait(driver, 15);

					wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("chat-secondary")));

					if (!chatTitle.getAttribute("title").contains(",")) {

						if (processMessage(chatTitle.getAttribute("title"))

								&& msgs.containsKey(chatTitle.getAttribute("title"))) {

							if ((!msgs.get(chatTitle.getAttribute("title"))

									.equals(removeSpecialCharacter(lastMsg.getText()))

									&& removeSpecialCharacter(lastMsg.getText()).length() != 0)

									|| msgs.get(chatTitle.getAttribute("title")).equals(removeSpecialCharacter("Photo"))

									|| msgs.get(chatTitle.getAttribute("title")).equals(removeSpecialCharacter("GIF"))

									|| (msgs.get(chatTitle.getAttribute("title")).contains(removeSpecialCharacter("/"))

											&& removeSpecialCharacter(lastMsg.getText()).length() == 21)) {

								try {

									System.out.println("Old Message="
											+ msgs.get(chatTitle.getAttribute("title")).length() + " New Message= "
											+ removeSpecialCharacter(lastMsg.getText()).length());

									actions.moveToElement(chat).click().perform();

									if (removeSpecialCharacter(lastMsg.getText()).equals(

											"?Messages you text to this chat and calls are secured with end-to-end encryption.")) {

										chat.click();

										// Thread.sleep(2000);

									}

									msgs.put(chatTitle.getAttribute("title"),

											removeSpecialCharacter(lastMsg.getText()));

									// if

									// (chatTitle.getAttribute("title").contains("pritish"))

									// {

									String a = chatTitle.getAttribute("title");

									try {

										chat.click();

										System.out.println(a);

									} catch (Exception e) {

										// TODO Auto-generated catch block

										actions.moveToElement(chat).click().perform();

										chat.click();

										System.out.println(chatTitle.getAttribute("title"));

										e.printStackTrace();

									}

									String message = messageReply(chatTitle.getAttribute("title"),

											removeSpecialCharacter(lastMsg.getText()));

									lastReply.put(chatTitle.getAttribute("title"), new Date());

									Thread.sleep(1000);

									if (!removeSpecialCharacter(lastMsg.getText()).equals("Photo")

											&& !removeSpecialCharacter(lastMsg.getText()).equals("GIF")

											&& !(removeSpecialCharacter(lastMsg.getText()).contains("/")

													&& removeSpecialCharacter(lastMsg.getText())

													.length() == 21)) {

										WebElement archi = driver.findElement(

												By.xpath("//*[@id='pane-side']/div/div/div/div/div/div/div[2]"));

										WebElement archTitle = archi.findElement(By.xpath(

												"//*[@id='pane-side']/div/div/div/div[1]/div/div/div[2]/div[1]/div[1]/span"));

										System.out.println(a.equals(archTitle.getText().toString().trim()));

										if (a.equals(archTitle.getText().toString().trim())) {

											actions.moveToElement(archi).click().perform();

											archi.click();

											actions.contextClick(archi).build().perform();

											wait.until(ExpectedConditions

													.visibilityOfElementLocated((By.className("dropdown"))));

											WebElement arch = driver.findElement(By.className("dropdown"));

											List<WebElement> archive = arch.findElements(By.tagName("li"));

											archive.get(0).click();

										} else {

											actions.moveToElement(chat).click().perform();

											chat.click();

											actions.contextClick(chat).build().perform();

											wait.until(ExpectedConditions

													.visibilityOfElementLocated((By.className("dropdown"))));

											WebElement arch = driver.findElement(By.className("dropdown"));

											List<WebElement> archive = arch.findElements(By.tagName("li"));

											archive.get(0).click();

										}
									}

								} catch (Exception e) {

									// TODO Auto-generated catch block

									e.printStackTrace();

								}

								// }

							} else {

								if (!removeSpecialCharacter(lastMsg.getText()).equals("Photo")

										&& !removeSpecialCharacter(lastMsg.getText()).equals("GIF")

										&& !(removeSpecialCharacter(lastMsg.getText()).contains("/")

												&& removeSpecialCharacter(lastMsg.getText())

												.length() == 21)) {

								}

							}

						} else {

							actions.moveToElement(chat).click().perform();

							if (removeSpecialCharacter(lastMsg.getText()).equals(

									"?Messages you text to this chat and calls are secured with end-to-end encryption.")) {

								chat.click();

								// Thread.sleep(2000);

							}

							System.out.println(

									"Length=" + removeSpecialCharacter(lastMsg.getText()).length());

							if (removeSpecialCharacter(lastMsg.getText()).length() > 0) {

								msgs.put(chatTitle.getAttribute("title"),

										removeSpecialCharacter(lastMsg.getText()));
								lastReply.put(chatTitle.getAttribute("title"), new Date());

								System.out.println("Starting Message=" + chatTitle.getAttribute("title") + " "
										+ removeSpecialCharacter(lastMsg.getText()));

							}

							String message = messageReply(chatTitle.getAttribute("title"),

									removeSpecialCharacter(lastMsg.getText()));

							WebElement archi = driver
									.findElement(By.xpath("//*[@id='pane-side']/div/div/div/div[1]/div/div/div[2]"));

							actions.moveToElement(archi).click().perform();

							chat.click();

							actions.contextClick(archi).build().perform();

							wait.until(ExpectedConditions.visibilityOfElementLocated((By.className("dropdown"))));

							WebElement arch = driver.findElement(By.className("dropdown"));

							List<WebElement> drop = arch.findElements(By.tagName("li"));

							/* WebElement archive; */

							try {

								drop.get(0).click();

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				} catch (Exception e) {

					// TODO Auto-generated catch block

					e.printStackTrace();

				}
			}

			inLoop = false;
		}

		// System.out.println("HDFC LOGGED IN");

		Thread.sleep(1500);

		// userList();
	}}

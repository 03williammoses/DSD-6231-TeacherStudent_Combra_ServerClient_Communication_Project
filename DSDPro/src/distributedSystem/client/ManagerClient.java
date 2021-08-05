package distributedSystem.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import distributedSystem.DCMSCorba.DCMSInterface;
import distributedSystem.DCMSCorba.DCMSInterfaceHelper;
import distributedSystem.model.Record;
import distributedSystem.model.StudentRecord;
import distributedSystem.model.TeacherRecord;
import distributedSystem.server.DollardServer;
import distributedSystem.server.LavalServer;
import distributedSystem.server.MontrealServer;

public class ManagerClient {

	static BufferedReader br;
	static DCMSInterface serverInstance;
	static NamingContextExt ncRef;
	private static Logger logger;

	public static void main(String[] args)
			throws IOException, NotBoundException, InterruptedException, ExecutionException, AlreadyBoundException, org.omg.CORBA.ORBPackage.InvalidName, NotFound, CannotProceed, InvalidName {

		br = new BufferedReader(new InputStreamReader(System.in));
		ORB orb = ORB.init(args, null);
		org.omg.CORBA.Object referenceObject = orb.resolve_initial_references("NameService");
		ncRef = NamingContextExtHelper.narrow(referenceObject);

		while (true) {
			System.out.println("Distributed Class Management System\n-----------------------------------------\n"
					+ "\nCreate User:-\n-----------" + "\n\nFormat to create user(Location suffixed with 4 digit number) Ex : MTL1234 :-  MTL---- / LVL---- / DDO----"
					 + "\n\nEnter UserName:");
			String uName = br.readLine().trim();
			if (uName(uName)) {
				logger = Logger.getLogger(uName);
				addLog("src/main/resources/logs/client/"+uName+".txt", uName);
				logger.info(uName+" user has successfully logged in");
				String managerPrefix = uName.substring(0, 3).toUpperCase();
				System.out.println("\nyou can proceed");
				String choice;
				Matcher matcher = null;
				do {
					System.out.println("\nChoose any of the options below to access the system\n1 : Create a Teacher Record\n2 : Create a Student Record"
									+ "\n3 : Edit a Record\n4 : Get Record Count\n5 : create new user\n6. Transfer Record: 7 : Exit");
					choice = br.readLine().trim().toUpperCase();
					
					switch (choice) {
					case "1":
						// create teacher record
						System.out.println("Enter teacher record inputs in the following format\n");
						System.out.println("Enter the FirstName : A-Z");
						String fName = "";
						boolean ask = true;
						while (ask) {
							fName = br.readLine();
							ask = !validateName(fName);
							if (ask)
							{
								System.out.println("Name does not purely contains alphabets. Give FirstName again!!");
								logger.info(fName+" Name does not purely contains alphabets. Give FirstName again!!");
							}
						}

						System.out.println("Enter the LastName : A-Z");
						String lName = "";
						ask = true;
						while (ask) {
							lName = br.readLine();
							ask = !validateName(lName);
							if (ask){
								System.out.println("Name does not purely contains alphabets. Give LastName again!!");
								logger.info(lName+" Name does not purely contains alphabets. Give LastName again!!");
							}
						}
						
						System.out.println("Enter the Address : A-Z-0-9");
						String address = "";
						ask = true;
						while(ask){
							address = br.readLine();
							ask = !validateAddress(address);
							if(ask){
								System.out.println("Address is not proper. Include door number seperated by  Streetname and so on...\n Ex : 123 SaintMarc");
								logger.info(address+" Address is not proper. Include door number seperated by  Streetname");
							}
						}
						
						System.out.println("Enter the Phone : 0-9");
						String phone = "";
						ask = true;
						while (ask) {
							phone = br.readLine();
							ask = !validateNumber(phone);
							if (ask){
								System.out.println("Phone number should contain 10 digits. Give number again!!");
								logger.info(phone+" Phone number should contain 10 digits. Give number again!!");
							}

						}
						System.out.println("Enter the Specialization : A-Z");
						String spec = "";
						ask = true;
						while (ask) {
							spec = br.readLine();
							ask = !validateName(spec);
							if (ask){
								System.out.println("Input does not purely contains alphabets. Give Specialization again!!");
								logger.info(spec+" Input does not purely contains alphabets");
							}
						}
						System.out.println("Enter the Location : MTL / LVL / DDO");
						String location = "";
						ask = true;
						while (ask) {
							location = br.readLine();
							ask = !validateLocation(location);
							if (ask)
							{
								System.out.println("Input should only be MTL or LVL or DDO. Try again!!");
								logger.info(location+" Input should only be MTL or LVL or DDO. Try again!!");
							}
						}

						createServerInstance(managerPrefix);
						String teacherSaveStatus = serverInstance.createTRecord(uName, fName, lName, address, phone, spec,
								location);
						System.out.println(teacherSaveStatus);
						logger.info(teacherSaveStatus);
						break;
					case "2":
						// create student record
						System.out.println("Enter Student record inputs in the following format");
						System.out.println("Enter the FirstName : A-Z");
						fName = "";
						ask = true;
						while (ask) {
							fName = br.readLine();
							ask = !validateName(fName);
							if (ask){
								System.out.println("Name does not purely contains alphabets. Give FirstName again!!");
								logger.info(fName+" Name does not purely contains alphabets");
							}

						}
						System.out.println("Enter the LastName : A-Z");
						lName = "";
						ask = true;
						while (ask) {
							lName = br.readLine();
							ask = !validateName(lName);
							if (ask){
								System.out.println("Name does not purely contains alphabets. Give LastName again!!");
								logger.info(lName+" Name does not purely contains alphabets.");
							}

						}

						String courses = "";
						ask = true;
						System.out.println("Enter courses seperated by comma");
						while (ask) {
							courses = br.readLine();
							ask = !validateCourses(courses);
							if (ask)
							{
								System.out.println("Course names should only contains letter and numbers, seperated by commas. Try again!!");
								logger.info(courses+" Course names should only contains letter and numbers, seperated by commas.");
							}
						}

						ask = true;
						String status = "";
						System.out.println("Enter the status, : 1 - Active / 0 - InActive ");
						while (ask) {
							status = br.readLine();
							ask = !validateStatus(status);
							if (ask)
							{
								System.out.println("Status should either be 0 or 1. Give status value again!!");
								logger.info(status+" Status should either be 0 or 1");
							}
						}

						ask = true;
						String date = "";
						System.out.println("Enter StatusDate : yyyy-mm-dd");
						while (ask) {
							date = br.readLine();
							ask = !validateDate(date);
							if (ask)
							{
								System.out.println("Input date format is wrong. Give date again!!");
								logger.info(date+" Input date format is wrong.");
							}
						}

						createServerInstance(managerPrefix);
						String studentSaveStatus = serverInstance.createSRecord(uName, fName, lName, courses, status, date);
						System.out.println(studentSaveStatus);
						logger.info(studentSaveStatus);
						break;
					case "3":
						System.out.println("Enter input to edit a record in the following format\n");
				
						System.out.println("Enter the record ID with TR/SR followed by 5 digits: TR----- or SR----- \n Ex: TR87283");
						String recordId = br.readLine();
						String fieldName = "";
						String thirdValue = "";
						boolean flag = true;
						if (recordId.startsWith("TR")) {
							System.out.println("Type which you want to edit : ADDRESS or PHONE or LOCATION\nExample : ADDRESS");
							fieldName = br.readLine();
							if (fieldName.equals("ADDRESS")) {
								System.out.println("Enter new adddress. Address should start with door no, seperated by spaces and other info.\n Ex: 123 Saint Marc");
								String newAddress = br.readLine();
								flag = validateAddress(newAddress.trim());
								thirdValue = newAddress;
							} else if (fieldName.equals("PHONE")) {
								System.out.println("Enter new phone number");
								String newPhone = br.readLine();
								flag = validateNumber(newPhone);
								thirdValue=newPhone;
							} else if (fieldName.equals("LOCATION")) {
								System.out.println("Enter new location");
								String newLocation = br.readLine();
								flag = validateLocation(newLocation);
								thirdValue=newLocation;
							} else {
								System.out.println("Error !! Input should only either ADDRESS or PHONE or LOCATION.");
								logger.info(fieldName+" Error !! Input should only either ADDRESS or PHONE or LOCATION.");
							}
						} else if (recordId.startsWith("SR")) {
							System.out.println(
									"Type the field you wanted to edit : COURSEREGISTERED or STATUS or STATUSDATE\nExample : STATUS");
							fieldName = br.readLine();
							if (fieldName.equals("COURSEREGISTERED")) {
								System.out.println("Enter new courses seperated by comma");
								String newCourses = br.readLine();
								flag = validateCourses(newCourses);
								thirdValue = newCourses;
							} else if (fieldName.equals("STATUS")) {
								System.out.println("Enter new status");
								String newStatus = br.readLine();
								flag = validateStatus(newStatus);
								thirdValue = newStatus;
							} else if (fieldName.equals("STATUSDATE")) {
								System.out.println("Enter new status date");
								String newDate = br.readLine();
								flag = validateDate(newDate);
								thirdValue = newDate;
							} else {
								System.out.println("Error !! Input should only either COURSEREGISTERED or STATUS or STATUSDATE.");
								logger.info(fieldName+" Error !! Input should only either COURSEREGISTERED or STATUS or STATUSDATE.");
							}
						} else {
							System.out.print("Input should either starts with SR or TR. Try again!!");
							logger.info(recordId+" Input should either starts with SR or TR. Try again!!");
						}
						if(flag) {
							createServerInstance(managerPrefix);
							System.out.println(serverInstance);
							String editStatus = serverInstance.editRecord(uName, recordId, fieldName, thirdValue);
							System.out.println(editStatus);
							logger.info(editStatus);
						}else {
							logger.info(thirdValue+" Input of the newValue contains invalid values");
						}
						break;
					case "4":
						createServerInstance(managerPrefix);
						String recordCount = serverInstance.getRecordCounts();
						System.out.println(recordCount);
						logger.info(recordCount);
						break;
					case "5":
						break;
					case "6":
						createServerInstance(managerPrefix);
						String recordStatus = transfer(br, managerPrefix, uName);
						System.out.println(recordStatus);
						break;
					case "7":
						System.out.println("Program Terminated!!");
						logger.info("Program Terminated!!");
						System.exit(0);
 					default:
						System.out.println("Invalid Choice");
						logger.info("Invalid Choice");
						continue;
					}
					Pattern pattern = Pattern.compile("^[1-2-3-4]{1}");
					matcher = pattern.matcher(choice);
				} while (matcher.matches() && !choice.equalsIgnoreCase("5"));
			} else {
				System.out.println("user name is invalid");
				logger.info(uName+" user name is invalid");
				continue;
			}
		}
	}
	
	public static String transfer(BufferedReader br, String managerPrefix, String uName) throws IOException, NotFound, CannotProceed, InvalidName, NotBoundException {
		
		String recordArray[]=serverInstance.listOfRecords().substring(1).split(",");
		List<String>recordList = new ArrayList<>();
		
		for(String str:recordArray)
			recordList.add(str);
		
		if(recordArray.length!=0) {
			System.out.println("The Available Records are : ");
			System.out.println(Arrays.toString(recordArray));
			
			System.out.println("Enter the record to shift : ");
			String recordID = br.readLine();
			
			while(!recordList.contains(recordID)) {
				System.out.println("Entered record is not present under this manger. Try again!!!");
				System.out.println("\nThe Available Records are : ");
				System.out.println(Arrays.toString(recordArray));
				
				System.out.println("Enter the record to shift : ");
				recordID = br.readLine();
			}
			
			System.out.println("Enter to which server you wish to move(MTL, LVL, DDO)");
			List<String> serverList=new ArrayList<>();
			serverList.add("MTL");
			serverList.add("LVL");
			serverList.add("DDO");
			String serverName = br.readLine();
			
			while(!serverList.contains(serverName)) {
				System.out.println("Server is not entered correctly...Try again (MTL, LVL, DDO)");
				serverName = br.readLine();
			}
			createServerInstance(serverName);
			return serverInstance.transferRecord(uName, recordID, managerPrefix);
			
		} else {
			System.out.println("No available records found!!!");
			return "Not transfered";
		}
		
	}

	public static Boolean uName(String uName) throws IOException {
		String CLIENT_LOG_URL = "src/resources/logs/client/";
		Pattern pattern = Pattern.compile("^[A-Za-z]{3}[0-9]{4}");
		Matcher matcher = pattern.matcher(uName);
		String[] Manager = { "MTL", "LVL", "DDO" };
		if (matcher.matches()) {
			String managerPrefix = uName.substring(0, 3).toUpperCase();
			for (String managerClient : Manager) {
				if (managerClient.equals(managerPrefix)) {
					File file = new File(CLIENT_LOG_URL + uName.toUpperCase() + ".txt");
					if (!file.exists()) {
						file.createNewFile();
						System.out.println(uName + " created");
						return true;
					} else {
						System.out.println(uName + " exists");
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean validateName(String name) {
		if (name == null)
			return false;
		for (int i = 0; i < name.length(); i++) {
			if (!Character.isLetter(name.charAt(i)))
				return false;
		}
		return true;
	}

	public static boolean validateNumber(String number) {
		if (number.length() != 10)
			return false;
		for (int i = 0; i < number.length(); i++) {
			if (!Character.isDigit(number.charAt(i)))
				return false;
		}
		return true;
	}

	public static boolean validateStatus(String status) {
		if (status.trim().equals("1") || status.trim().equals("0"))
			return true;
		return false;
	}

	public static boolean validateDate(String date) {
		Pattern pattern = Pattern.compile("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$");
		Matcher matcher = pattern.matcher(date);
		if (matcher.matches())
			return true;
		return false;
	}

	public static boolean validateCourses(String courses) {
		Pattern pattern = Pattern.compile("^(?:[a-zA-Z0-9]+(?:,[a-zA-Z0-9]+)*)?$");
		Matcher matcher = pattern.matcher(courses);
		if (matcher.matches())
			return true;
		return false;
	}

	public static boolean validateLocation(String location) {
		if (location.equals("MTL") || location.equals("LVL") || location.equals("DDO"))
			return true;
		return false;
	}
	
	public static boolean validateAddress( String address )
	{
	      return address.matches( 
	         "\\d+\\s+([a-zA-Z]+|[a-zA-Z]+\\s[a-zA-Z]+)" );
	} 

	public static void createServerInstance(String server) throws AccessException, RemoteException, NotBoundException, NotFound, CannotProceed, InvalidName {
		if ("MTL".equalsIgnoreCase(server)) {
			serverInstance = DCMSInterfaceHelper.narrow(ncRef.resolve_str("Montreal"));
		}
		if ("LVL".equalsIgnoreCase(server)) {
			serverInstance = DCMSInterfaceHelper.narrow(ncRef.resolve_str("Laval"));
		}
		if ("DDO".equalsIgnoreCase(server)) {
			serverInstance = DCMSInterfaceHelper.narrow(ncRef.resolve_str("Dollard-des-Ormeaux"));
		}
	}
	
	static void addLog(String path, String key) {
		try {
			File f = new File(path);
			String data = "";
			logger = Logger.getLogger(key);
			if (f.exists() && !f.isDirectory()) {
				data = new String(Files.readAllBytes(Paths.get(path)));
			}
			if (logger.getHandlers().length < 1) {
				try {
					f.delete();
				} catch (Exception e) {
				}
				logger = Logger.getLogger(key);
				FileHandler fh = new FileHandler(path, true);
				SimpleFormatter ft = new SimpleFormatter();
				fh.setFormatter(ft);
				logger.addHandler(fh);
				logger.setUseParentHandlers(false);
				logger.info(data);
				logger.setUseParentHandlers(true);
			}
		} catch (Exception err) {
			logger.info("Unable to create file, please check file permission.");
		}
	}

}

package distributedSystem.server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.omg.CORBA.ORB;

import distributedSystem.DCMSCorba.DCMSInterfacePOA;
import distributedSystem.model.Record;
import distributedSystem.model.StudentRecord;
import distributedSystem.model.TeacherRecord;

public class DollardServer extends DCMSInterfacePOA{

	public static List<String> TEACHER_INFO = Arrays.asList("Address", "Phone", "Location");
	public static List<String> STUDENT_INFO = Arrays.asList("Course Registered", "Status", "Status Date");
	public HashMap<String, List<Record>> teacherStudentRecords = new HashMap<>();
	private String location;
	private int port;
	private static final long serialVersionUID = 1L;
	private ORB orb;
	public void setORB(ORB orb_val) {
			orb = orb_val;
	}
	
	protected DollardServer(String location) throws RemoteException {
		teacherStudentRecords = new HashMap<>();
		List<Record> ar = new ArrayList<>();
		ar.add(new TeacherRecord("DDO1234", "A", "B", "123 C", "1234567890", "CS", "MTL"));
		teacherStudentRecords.put("A", ar);
		this.location = location;
	}
	
	protected DollardServer(){
		teacherStudentRecords = new HashMap<>();
		List<Record> ar = new ArrayList<>();
		ar.add(new TeacherRecord("DDO1234", "A", "B", "123 C", "1234567890", "CS", "MTL"));
		teacherStudentRecords.put("A", ar);
		this.location = "Dollard-des-Ormeaux";
	}

	public static Logger logger = Logger.getLogger("Dollard");

	
	public void serverConnection(int port, DollardServer dollardServ) throws RemoteException, AlreadyBoundException {
		addLog("src/main/resources/logs/server/Dollard.txt", "Dollard");
		logger.info("Dollard-des-Ormeaux Server Started");
		DatagramSocket ds = null;
		

		while (true) {
			try {
				ds = new DatagramSocket(port);
				byte[] receive = new byte[6553500];
				DatagramPacket dp = new DatagramPacket(receive, receive.length);
				ds.receive(dp);
				byte[] data = dp.getData();
				String serviceName = new String(data);
				String outputStr = "";
				if(serviceName.trim().equalsIgnoreCase("getCount")) 
				{
					int mapSize = 0;
					for(Entry<String, List<Record>> list : teacherStudentRecords.entrySet()) {
						mapSize+=list.getValue().size();
					}
					outputStr = "DDO : " + mapSize;
				}
				else
					outputStr = this.getRecord(serviceName.trim());
				DatagramPacket dp1 = new DatagramPacket(outputStr.getBytes(), outputStr.length(), dp.getAddress(),
						dp.getPort());
				ds.send(dp1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ds.close();
			}
		}
	}
	

	@SuppressWarnings("unlikely-arg-type")
	@Override
	synchronized public String createTRecord(String managerID, String firstName, String lastName, String address, String phone,
			String specialization, String location) {

		Record teacherRecord = new TeacherRecord(managerID, firstName, lastName, address, phone, specialization, location);

		if (teacherStudentRecords.isEmpty()
				|| !teacherStudentRecords.containsKey(Character.toUpperCase(lastName.charAt(0))+"")) {
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(lastName.charAt(0))),
					Arrays.asList(teacherRecord));
		} else if (teacherStudentRecords.containsKey(Character.toUpperCase(lastName.charAt(0))+"")) {
			teacherStudentRecords.get(Character.toString(Character.toUpperCase(lastName.charAt(0)))).add(teacherRecord);
		}

		System.out.println("Teacher record is created in " + this.location);
		logger.info("Teacher record is created in " + this.location);
		return "Teacher record is created in " + this.location;
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	synchronized public String createSRecord(String managerID, String firstName, String lastName, String courseRegistered, String status,
			String statusDate) {
		Record studentRecord = new StudentRecord(managerID, firstName, lastName, courseRegistered, status, statusDate);

		if (teacherStudentRecords.isEmpty()
				|| !teacherStudentRecords.containsKey(Character.toUpperCase(lastName.charAt(0))+"")) {
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(lastName.charAt(0))),
					Arrays.asList(studentRecord));
		} else if (teacherStudentRecords.containsKey(Character.toUpperCase(lastName.charAt(0))+"")) {
			teacherStudentRecords.get(Character.toString(Character.toUpperCase(lastName.charAt(0)))).add(studentRecord);
		}

		System.out.println("Student record is created in " + this.location);
		logger.info("Student record is created in " + this.location);
		return "Student record is created in " + this.location;
	}
	

	@Override
	synchronized public String editRecord(String managerID, String recordID, String fieldName, String newValue) {

		if (recordID.contains("SR")) {
			if (existsInList(STUDENT_INFO, fieldName)) {
				return updateStudentRecord(recordID, fieldName, newValue);
			} else {
				return "Given field name does not exists for student record or it is not allowed to modify the given field.";
			}
		} else if (recordID.contains("TR")) {
			if (existsInList(TEACHER_INFO, fieldName)) {
				return updateTeacherRecord(recordID, fieldName, newValue);
			} else {
				return "Given field name does not exists for student record or it is not allowed to modify the given field.";
			}
		} else {
			return "Invalid Input";
		}
	}

	private String updateTeacherRecord(String recordID, String fieldName, String newValue) {
		String resultMsg = null;
		boolean result = false;
		for (Entry<String, List<Record>> entry : teacherStudentRecords.entrySet()) {
			for (Object object : entry.getValue()) {
				if (object instanceof TeacherRecord
						&& ((TeacherRecord) object).getRecordID().equalsIgnoreCase(recordID)) {
					if (fieldName.equalsIgnoreCase("Address")) {
						((TeacherRecord) object).setAddresss(newValue);
						result = true;
					} else if (fieldName.equalsIgnoreCase("Phone")) {
						((TeacherRecord) object).setPhone(newValue);
						result = true;
					} else if (fieldName.equalsIgnoreCase("Location")) {
						if (newValue.equalsIgnoreCase("mtl") || newValue.equalsIgnoreCase("lvl")
								|| newValue.equalsIgnoreCase("ddo")) {
							((TeacherRecord) object).setPhone(newValue.toLowerCase());
							result = true;
						} else {
							resultMsg = "Required Operation is not perfomed due to invalid location given. \nPlease provide locations from : MTL, LVL or DDO.";
						}
					}
				}
			}
		}
		if (result)
			return "Required Operation is performed successfully.";

		return !result && resultMsg != null ? resultMsg
				: "Teacher Record with given id : " + recordID
						+ " was not found. Required update operation is not performed.";
	}

	private String updateStudentRecord(String recordID, String fieldName, String newValue) {
		boolean result = false;

		for (Entry<String, List<Record>> entry : teacherStudentRecords.entrySet()) {
			for (Object object : entry.getValue()) {
				if (object instanceof StudentRecord
						&& ((StudentRecord) object).getRecordID().equalsIgnoreCase(recordID)) {
					if (fieldName.equalsIgnoreCase("Course Registered")) {
						((StudentRecord) object).setCoursesRegistered(newValue);
						result = true;
					} else if (fieldName.equalsIgnoreCase("Status")) {
						((StudentRecord) object).setStatus(newValue);
						result = true;
					} else if (fieldName.equalsIgnoreCase("StatusDate")) {
						LocalDate localDate = LocalDate.parse(newValue);
						((StudentRecord) object).setStatusDate(localDate);
						result = true;
					}
				}
			}
		}
		return result ? "Required Operation is performed successfully."
				: "Student Record with given id : " + recordID
						+ " was not found. Required update operation is not performed.";
	}
	
//
//	public String call(){
//
//		DatagramSocket socket = null;
//		try {
//			byte[] b = new byte[65535];
//			String request = "getRecordCounts";
//			socket = new DatagramSocket();
//			DatagramPacket packetToSend = new DatagramPacket(request.getBytes(), request.getBytes().length,
//					InetAddress.getByName("localhost"), port);
//			socket.send(packetToSend);
//			DatagramPacket recievedPacket = new DatagramPacket(b, b.length);
//			socket.receive(recievedPacket);
//			String returnData = new String(recievedPacket.getData());
//			return returnData.trim();
//		} catch (UnknownHostException e) {
//			logger.info(e.getMessage());
//		} catch (SocketException e) {
//			logger.info(e.getMessage());
//		} catch (IOException e) {
//			logger.info(e.getMessage());
//		} finally {
//			socket.close();
//		}
//		return "";
//	}

	private boolean existsInList(List<String> fieldList, String keyToCheck) {
		for (String field : fieldList) {
			if (keyToCheck.equalsIgnoreCase(field))
				return true;
		}
		return false;
	}

	synchronized public String getRecordCounts() {
		String server1 = "";
		String server2 = "";
		String server3 = "";
		int mapSize = 0;
		for(Entry<String, List<Record>> list : teacherStudentRecords.entrySet()) {
			mapSize+=list.getValue().size();
		}
		server1 += "DDO : " + mapSize;
		server2 += this.DatafromOtherServers(8880);
		server3 += this.DatafromOtherServers(8881);

		return server1 + ", " + server2 + ", " + server3;
	}


	public String DatafromOtherServers(int port) {

		try (DatagramSocket socket = new DatagramSocket();) {

			byte[] b = new byte[65535];
			String request = "getCount";

			TimeUnit.MILLISECONDS.sleep(20);
			DatagramPacket packetToSend = new DatagramPacket(request.getBytes(), request.getBytes().length,
					InetAddress.getByName("localhost"), port);
			socket.send(packetToSend);

			DatagramPacket recievedPacket = new DatagramPacket(b, b.length);
			socket.receive(recievedPacket);
			String returnData = new String(recievedPacket.getData());
			return returnData.trim();
		} catch (IOException | InterruptedException e) {
			return null;
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

	@Override
	public String listOfRecords() {
		String listNames = "";
		for(Entry<String, List<Record>> map:teacherStudentRecords.entrySet()) {
			for(Record recordNames : map.getValue()) {
				if(recordNames instanceof TeacherRecord) {
					listNames = listNames +","+ ((TeacherRecord) recordNames).getRecordID();
				}else if(recordNames instanceof StudentRecord) {
					listNames = listNames +","+ ((StudentRecord) recordNames).getRecordID();
				}
			}
		}
		return listNames;
	}


	@Override
	public String getRecord(String recordID) {
		String result = null;
		Record deleteRecord = null;
		String deleteString = "";
		boolean flag = false;
		for(Entry<String, List<Record>> list : teacherStudentRecords.entrySet()) {
			for(Record map : list.getValue()) {
				if(map instanceof TeacherRecord) {
					TeacherRecord tr= (TeacherRecord) map;
					if(tr.getRecordID().equalsIgnoreCase(recordID))
					{
						deleteString = list.getKey();
						deleteRecord = tr;
						result = tr.getRecordID()+","+tr.getFirstName()+","+tr.getLastName()+","+tr.getPhone()+","+tr.getAddresss()+","+tr.getSpecilization()+","+tr.getLocation();		
						flag = true;
					}
				}else {
					StudentRecord tr= (StudentRecord) map;
					if(tr.getRecordID().equalsIgnoreCase(recordID))
					{
						deleteString = list.getKey();
						deleteRecord = tr;
						result = tr.getRecordID()+","+tr.getFirstName()+","+tr.getLastName()+","+tr.getCoursesRegistered()+","+tr.getStatusDate()+","+tr.getStatus();		
						flag = true;
					}
				}
				if(flag)
					break;
			}
			if(flag)
				break;
		}
		if(flag) {
			teacherStudentRecords.get(deleteString).remove(deleteRecord);
			return result;
		}else {
			return null;
		}
	}
	
	public String StringFromOtherServers(int port, String recordID) {

		try (DatagramSocket socket = new DatagramSocket();) {

			byte[] b = new byte[6553500];
			String request = String.valueOf(recordID);

			TimeUnit.MILLISECONDS.sleep(20);
			DatagramPacket packetToSend = new DatagramPacket(request.getBytes() , request.getBytes().length,
					InetAddress.getByName("localhost"), port);
			socket.send(packetToSend);

			DatagramPacket recievedPacket = new DatagramPacket(b, b.length);
			socket.receive(recievedPacket);
			String returnData = new String(recievedPacket.getData());
			return returnData.trim();
		} catch (IOException | InterruptedException e) {
			return null;
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
		String[] record ;
		if(remoteCenterServerName.equalsIgnoreCase("MTL")) {
			record = StringFromOtherServers(8880, recordID).split(",");
		}else {
			record = StringFromOtherServers(8881, recordID).split(",");
		}
		Record recordObject = null;
		if(record[0].startsWith("TR")) {
			recordObject = new TeacherRecord(managerID, record[1], record[2], record[4], record[3], record[5], record[6]);
			((TeacherRecord) recordObject).setRecordID(record[0]);
			TeacherRecord.idCounter--;
		}else {
			recordObject = new StudentRecord(managerID, record[1], record[2], record[3], record[4], record[5]);
			((StudentRecord) recordObject).setRecordID(record[0]);
			StudentRecord.idCounter--;
		}
		if (teacherStudentRecords.isEmpty()
				|| !teacherStudentRecords.containsKey(Character.toUpperCase(record[2].charAt(0))+"")) {
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(record[2].charAt(0))),
					Arrays.asList(recordObject));
		} else if (teacherStudentRecords.containsKey(Character.toUpperCase(record[2].charAt(0))+"")) {
			teacherStudentRecords.get(Character.toString(Character.toUpperCase(record[2].charAt(0)))).add(recordObject);
		}
		
		return recordObject!=null?"Successfully transfered":"Not transfered";
	}
}

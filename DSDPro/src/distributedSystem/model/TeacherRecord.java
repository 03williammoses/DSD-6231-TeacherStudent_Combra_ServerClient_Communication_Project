package distributedSystem.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherRecord extends Record{


	private String managerID;
	private String firstName;
	private String lastName;
	private String addresss;
	private String phone;
	private String specilization;
	private String location;
	public static int idCounter = 9999;
	private String recordID;

	public TeacherRecord(String managerID, String firstName, String lastName, String address, String phone, String specilization,
			String location) {

		this.managerID = managerID;
		this.recordID = "TR" + (++idCounter);
		this.firstName = firstName;
		this.lastName = lastName;
		this.addresss = address;
		this.phone = phone;
		this.specilization = specilization;
		this.location = location;
	}
	
	
}

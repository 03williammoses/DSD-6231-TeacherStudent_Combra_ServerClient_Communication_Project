package distributedSystem.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRecord extends Record{

	private String managerID;
	private String firstName;
	private String lastName;
	private String coursesRegistered;
	private String status;
	private LocalDate statusDate;
	public static int idCounter = 9999;
	private String recordID;
	
	public StudentRecord(String managerID, String firstName, String lastName, String coursesRegistered, String status, String statusDate) {
		
		this.managerID = managerID;
		this.recordID = "SR" + (++idCounter);
		this.firstName = firstName;
		this.lastName = lastName;
		this.coursesRegistered = coursesRegistered;
		this.status = status;
		LocalDate localDate = LocalDate.parse(statusDate);
		this.statusDate = localDate;
	}
}

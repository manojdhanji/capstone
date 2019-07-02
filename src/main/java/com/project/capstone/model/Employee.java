package com.project.capstone.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Employee implements Serializable {

	static final long serialVersionUID = 5845133715707324168L;
	private String empId;
	private String lastName;
	private String firstName;
	private String email;
	Map<LocalDate, Set<Shift>> shifts = new HashMap<>();
	
	/**
	 * @return the empId
	 */
	public String getEmpId() {
		return empId;
	}
	/**
	 * @param empId the empId to set
	 */
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void addShift(LocalDate workingDate, Shift shift) {
		if(Objects.nonNull(workingDate) &&
				Objects.nonNull(shift)) {
			shifts.computeIfPresent(workingDate, (k,v)->{v.add(shift);return v;});
			shifts.computeIfAbsent(workingDate, (set)->new HashSet<>()).add(shift);
		}
	}
	public Map<LocalDate, Set<Shift>> getShifts(){
		return shifts;
	}
	public Set<Shift> getShifts(LocalDate workingDate){
		if(Objects.nonNull(workingDate)) {
			return shifts.entrySet().stream().filter(e->e.getKey().compareTo(workingDate)==0).map(e->e.getValue()).findFirst().orElse(Collections.emptySet());
		}
		return Collections.emptySet();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((empId == null) ? 0 : empId.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (empId == null) {
			if (other.empId != null)
				return false;
		} else if (!empId.equals(other.empId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder("Employee: {") ;
		buffer.append(" empId: ")
			  .append(empId)
			  .append(", firstName: ")
			  .append(firstName)
			  .append(", lastName: ")
			  .append(lastName)
			  .append(", email: ")
			  .append(email)
			  .append(", shifts")
			  .append(shifts);
		return buffer.toString();
	}
}

package com.project.capstone.model;

import java.io.Serializable;
import java.time.LocalTime;

public class Shift implements Serializable {

	static final long serialVersionUID = -8726503990199573560L;
	private int shiftId;
	private LocalTime shiftStartTime;
	private LocalTime shiftEndTime;
	public Shift() {
		
	}
	public Shift(int shiftId, LocalTime shiftStartTime, LocalTime shiftEndTime) {
		this.setShiftId(shiftId);
		this.setShiftStartTime(shiftStartTime);
		this.setShiftEndTime(shiftEndTime);
	}
	/**
	 * @return the shiftId
	 */
	public int getShiftId() {
		return shiftId;
	}
	/**
	 * @param shiftId the shiftId to set
	 */
	public void setShiftId(int shiftId) {
		this.shiftId = shiftId;
	}
	/**
	 * @return the shiftStartTime
	 */
	public LocalTime getShiftStartTime() {
		return shiftStartTime;
	}
	/**
	 * @param shiftStartTime the shiftStartTime to set
	 */
	public void setShiftStartTime(LocalTime shiftStartTime) {
		this.shiftStartTime = shiftStartTime;
	}
	/**
	 * @return the shiftEndTime
	 */
	public LocalTime getShiftEndTime() {
		return shiftEndTime;
	}
	/**
	 * @param shiftEndTime the shiftEndTime to set
	 */
	public void setShiftEndTime(LocalTime shiftEndTime) {
		this.shiftEndTime = shiftEndTime;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + shiftId;
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
		Shift other = (Shift) obj;
		if (shiftId != other.shiftId)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Shift: {shiftId:" + shiftId + ", shiftStartTime:" + shiftStartTime + ", shiftEndTime:" + shiftEndTime + "}";
	}
}

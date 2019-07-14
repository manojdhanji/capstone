package com.project.capstone.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.project.capstone.dao.EmployeeDao;
import com.project.capstone.model.Employee;

@Service
public class EmployeeService {
	@Autowired
	@Qualifier("employeeDao")
	private EmployeeDao employeeDao;
	public int beginEmployeeShift(String id, int shiftId, LocalDateTime workingDateTime) {
		return employeeDao.insertEmployeeShift(id,shiftId,workingDateTime);
	}
	public int endEmployeeShift(String id) {
		return employeeDao.updateEmployeeShift(id);
	}
	public Optional<Employee> findEmployeeShifts(String id, LocalDate workingDate) {
		return employeeDao.findEmployeeShifts(id,workingDate);
	}
	public List<Employee> findAllEmployeesShifts(LocalDate startDate, LocalDate endDate) {
		return employeeDao.findAllEmployeesShifts(startDate, endDate);
	}
	public Optional<Employee> findEmployeeShifts(String id, LocalDate startDate, LocalDate endDate) {
		return employeeDao.findEmployeeShifts(id,startDate, endDate);
	}
	
	public int insertEmployee(Employee e) {
		return employeeDao.insertEmployee(e);
	}
	public Optional<Employee> findEmployee(String id) {
		return employeeDao.findEmployee(id);
	}
	public List<Employee> getEmployees(){
		return employeeDao.findEmployees();
	}
}

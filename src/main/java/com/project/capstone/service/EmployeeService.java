package com.project.capstone.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
	public int endEmployeeShift(String id, int shiftId, LocalDateTime workingDateTime) {
		return employeeDao.updateEmployeeShift(id,shiftId,workingDateTime);
	}
	public Optional<Employee> findEmployeeShifts(String id, LocalDate workingDate) {
		return employeeDao.findEmployeeShifts(id,workingDate);
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
		List<Employee> employees = employeeDao.findEmployees();
		if(employees.isEmpty()) {
			throw new ResponseStatusException(
			          HttpStatus.NOT_FOUND, "No Employees Found", null);
		}
		return employees;
	}
}
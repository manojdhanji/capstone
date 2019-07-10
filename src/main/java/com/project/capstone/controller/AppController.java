package com.project.capstone.controller;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.project.capstone.exception.AddEmployeeException;
import com.project.capstone.exception.ClockInException;
import com.project.capstone.exception.ClockOutException;
import com.project.capstone.exception.NonExistentEntityException;
import com.project.capstone.model.ClockState;
import com.project.capstone.model.Employee;
import com.project.capstone.model.Shift;
import com.project.capstone.service.EmployeeService;
import com.project.capstone.service.ShiftService;
import com.project.capstone.utils.Constants;

@RestController
public class AppController {
	private Logger log = LoggerFactory.getLogger(AppController.class);
	@Autowired
	@Qualifier("employeeService")
	private EmployeeService employeeService;
	
	
	@Autowired
	@Qualifier("shiftService")
	private ShiftService shiftService;
	
	@GetMapping(path="/capstone/shifts")
	public List<Shift> getShifts(){
		return shiftService.findShifts();
	}
	
	@PatchMapping(path="/capstone/employees/{id}/clockout")
	public ClockState clockOutEmployee(
			@PathVariable("id") String id, 
				/*@RequestParam ("workingDate") String workingDate,*/
					@RequestParam ("shiftId") int shiftId) {
		try {
			
			/*LocalDateTime ldt= LocalDateTime.of(LocalDate.parse(workingDate, DateTimeFormatter.BASIC_ISO_DATE),
													LocalTime.now());*/
			//LocalDateTime ldt = LocalDateTime.now();
			employeeService.endEmployeeShift(id,shiftId);
			return ClockState.CLOCK_OUT;
		}
		catch(DateTimeParseException dtpe) {
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Date must be in yyyyMMdd format", null);
		}
		catch(ClockOutException ce) {
			throw new ResponseStatusException(
					HttpStatus.PRECONDITION_FAILED, ce.getMessage(), ce);
		}
		catch(NonExistentEntityException ne) {
			throw new ResponseStatusException(
					HttpStatus.FAILED_DEPENDENCY, ne.getMessage(), ne);
		}
	}
	
	@PostMapping(path="/capstone/employees/{id}/clockin")
	public ClockState clockInEmployee(
			@PathVariable("id") String id, 
				/*@RequestParam ("workingDate") String workingDate,*/
					@RequestParam ("shiftId") int shiftId) {
		
		try {
			/*LocalDateTime ldt= LocalDateTime.of(LocalDate.parse(workingDate, DateTimeFormatter.BASIC_ISO_DATE),
													LocalTime.now());*/
			
			LocalDateTime ldt = LocalDateTime.now();
			employeeService.beginEmployeeShift(id,shiftId,ldt);
			return ClockState.CLOCK_IN;
		}
		catch(DateTimeParseException dtpe) {
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Date must be in yyyyMMdd format", null);			
		}
		catch(ClockInException ce) {
			throw new ResponseStatusException(
					HttpStatus.PRECONDITION_FAILED, ce.getMessage(), ce);
		}
		catch(DuplicateKeyException de) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT, "Employee is already clocked in for this shift", de);
		}
		catch(NonExistentEntityException neee) {
			throw new ResponseStatusException(
					HttpStatus.FAILED_DEPENDENCY, neee.getMessage(), neee);
		}
	}
	
	@PostMapping(path="/capstone/employees")
	public boolean addEmployee(
			@RequestParam("id") String id,
				@RequestParam("firstName") String firstName,
					@RequestParam("lastName") String lastName,
						@RequestParam("email") String email) {
		if(StringUtils.isNotBlank(id) && 
				Constants.EMP_ID_REGEX.matcher(id).matches()) {
			Employee e = new Employee();
			e.setEmpId(id);
			e.setFirstName(firstName);
			e.setLastName(lastName);
			e.setEmail(email);
			try {
				return employeeService.insertEmployee(e)!=0?Boolean.TRUE:Boolean.FALSE;
			}
			catch(AddEmployeeException ae) {
				throw new ResponseStatusException(
						HttpStatus.EXPECTATION_FAILED,
							ae.getMessage(), ae);
			}
			catch(DuplicateKeyException de) {
				throw new ResponseStatusException(
						HttpStatus.CONFLICT,
							"Employee already exists", de);
			}
		}
		throw new ResponseStatusException(
				HttpStatus.EXPECTATION_FAILED,
					MessageFormat.format("Employee ID does not match {0}",Constants.EMP_ID_REGEX), null);
	}
	@GetMapping(path="/capstone/employees/{id}")
	public Employee getEmployee(@PathVariable("id") String id) {
		log.info("id: {}",id);
		Optional<Employee> optEmployee = Optional.<Employee>empty();
		optEmployee = employeeService.findEmployee(id);
		if(optEmployee.isPresent()) {
			return optEmployee.get();
		}
		throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, "Employee Does Not Exist", null);
	}
	
	@GetMapping(path="/capstone/employees/{id}/shifts")
	public Employee getEmployeeShifts(
			@PathVariable("id") String id,
				@RequestParam("startDate") String startDate,
					@RequestParam("endDate") Optional<String> optEndDate) {
		
		log.info("id: {} startDate: {} endDate: {}",id, startDate, optEndDate);
		
		try {
			Optional<Employee> optEmployee = Optional.<Employee>empty();
			LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
			if(optEndDate.isPresent()) {
				LocalDate end = LocalDate.parse(optEndDate.get(), DateTimeFormatter.BASIC_ISO_DATE);
				optEmployee = employeeService.findEmployeeShifts(id, start, end);
			}
			else
				optEmployee = employeeService.findEmployeeShifts(id, start);
			
			if(optEmployee.isPresent()) {
				return optEmployee.get();
			}
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Employee Not Clocked In", null);
			
		}
		catch(DateTimeParseException dtpe) {
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Date must be in yyyyMMdd format", null);
		}
		catch(NonExistentEntityException ndfe) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Employee Does Not Exist (Referential Integrity Exception)", null);
		}
	}
	
	@GetMapping(path="/capstone/employees/shifts")
	public List<Employee> getAllEmployeesShifts(
			@RequestParam("startDate") String startDate,
				@RequestParam("endDate") String endDate) {

		log.info("{} startDate: {} endDate: {}", startDate, endDate);

		try {
			LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
			LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.BASIC_ISO_DATE);
			return employeeService.findAllEmployeesShifts(start, end);
		}
		catch(DateTimeParseException dtpe) {
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Date must be in yyyyMMdd format", null);
		}
	}

	@GetMapping(path="/capstone/employees")
	public List<Employee> getEmployees(){
		return employeeService.getEmployees();
	}
}

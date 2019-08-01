package com.project.capstone.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

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
import com.project.capstone.utils.ShiftUtils;

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
	@GetMapping(path="/capstone/shifts/closest")
	public int findClosestShift(){
		try {
		 return shiftService.findClosestShift().getShiftId();
		}
		catch(Exception e) {
			return -1;
		}
	}
	
	@PatchMapping(path="/capstone/employees/{id}/clockout")
	public ClockState clockOutEmployee(@PathVariable("id") String id) {
		try {
			if(Constants.EMP_ID_PATTERN_MATCH.test(id)) {
				employeeService.endEmployeeShift(id);
				return ClockState.CLOCK_OUT;
			}
			else
				throw new IllegalArgumentException("Employee Id format required: EMP-XXXX");
		}
		catch(ClockOutException ce) {
			throw new ResponseStatusException(
					HttpStatus.PRECONDITION_FAILED, ce.getMessage(), ce);
		}
		catch(NonExistentEntityException ne) {
			throw new ResponseStatusException(
					HttpStatus.FAILED_DEPENDENCY, ne.getMessage(), ne);
		}
		catch(IllegalArgumentException ie) {
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, ie.getMessage(), ie);
		}
	}
	
	@PostMapping(path="/capstone/employees/{id}/clockin")
	public ClockState clockInEmployee(
			@PathVariable("id") String id, 
				@RequestParam ("shiftId") int shiftId) {
		try {
			if(!Constants.EMP_ID_PATTERN_MATCH.test(id)) 
				throw new IllegalArgumentException("Employee Id format required: EMP-XXXX");
			if(shiftService.findShifts().stream().noneMatch(s->s.getShiftId()==shiftId))
				throw new IllegalArgumentException("Invalid shift Id");
			LocalDateTime ldt = LocalDateTime.now();
			employeeService.beginEmployeeShift(id,shiftId,ldt);
			return ClockState.CLOCK_IN;
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
		catch(IllegalArgumentException ie) {
			throw new ResponseStatusException(
				HttpStatus.EXPECTATION_FAILED, ie.getMessage(), ie);
		}
	}
	
	@DeleteMapping(path="/capstone/employees/{id}")
	public int deleteEmployee(@PathVariable ("id") String id) {
		if(!Constants.EMP_ID_PATTERN_MATCH.test(id)){
			throw new ResponseStatusException(
				HttpStatus.EXPECTATION_FAILED, "Employee Id format required: EMP-XXXX", null); 
		}
		return employeeService.deleteEmployee(id);
	}
	
	@PostMapping(path="/capstone/employees")
	public int addEmployee(
			@RequestParam("id") String id,
				@RequestParam("firstName") String firstName,
					@RequestParam("lastName") String lastName,
						@RequestParam("email") String email) {
		if(Constants.EMP_ID_PATTERN_MATCH.test(id) && 
			Constants.EMAIL_PATTERN_MATCH.test(email) &&
				StringUtils.isNotBlank(firstName) &&
					StringUtils.isNotBlank(firstName)) {
			Employee e = new Employee();
			e.setEmpId(id);
			e.setFirstName(firstName);
			e.setLastName(lastName);
			e.setEmail(email);
			try {
				return employeeService.insertEmployee(e);
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
					"Input data must be valid", null);
	}
	
	@PutMapping(path="/capstone/employees/{id}")
	public int updateEmployee(
		@PathVariable ("id") String id,
			@RequestParam("firstName") String firstName,
				@RequestParam("lastName") String lastName,
					@RequestParam("email") String email) {
		if(!Constants.EMP_ID_PATTERN_MATCH.test(id)) 
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Employee Id format required: EMP-XXXX", null); 
		try {
			if(StringUtils.isNotBlank(firstName) &&
					StringUtils.isNotBlank(lastName) &&
						StringUtils.isNotBlank(email) && Constants.EMAIL_REGEX.matcher(email).matches()) {
				
				return employeeService.updateEmployee(id, firstName, lastName, email);
			}
			else {
				throw new IllegalArgumentException("Please check update parameters");
			}
		}
		catch(IllegalArgumentException ie) {
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, ie.getMessage(), ie);
		}
	}
	
	@GetMapping(path="/capstone/employees/{id}")
	public Employee getEmployee(@PathVariable("id") String id) {
		Optional<Employee> optEmployee = Optional.<Employee>empty();
		if(!Constants.EMP_ID_PATTERN_MATCH.test(id)) 
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Employee Id format required: EMP-XXXX", null); 
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
		try {
			Optional<Employee> optEmployee = Optional.<Employee>empty();
			if(!Constants.EMP_ID_PATTERN_MATCH.test(id)) 
				throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Employee Id format required: EMP-XXXX", null); 
			log.info("id: {} startDate: {} endDate: {}",id, startDate, optEndDate);
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
	@PutMapping( "/capstone/employees/{id}/shifts/{shiftId}")
	public int updateEmployeeShift(
			@PathVariable("id") String id,
				@PathVariable("shiftId") int shiftId,
					@RequestParam("newShiftId") int newShiftId,
						@RequestParam ("newShiftStartTime") String newShiftStartTime,
							@RequestParam("newShiftEndTime") String newShiftEndTime,
								@RequestParam("workingDate") String workingDate) {
		try {
			if(!Constants.EMP_ID_PATTERN_MATCH.test(id)) 
				throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Employee Id format required: EMP-XXXX", null);
			if(shiftService.findShifts().stream().noneMatch(s->s.getShiftId()==shiftId))
				throw new IllegalArgumentException("Invalid shift Id");
			if(shiftService.findShifts().stream().noneMatch(s->s.getShiftId()==newShiftId))
				throw new IllegalArgumentException("Invalid new shift Id");
			
			LocalTime startTime = LocalTime.parse(newShiftStartTime, DateTimeFormatter.ofPattern("HHmm"));
			LocalTime endTime = LocalTime.parse(newShiftEndTime, DateTimeFormatter.ofPattern("HHmm"));
			LocalDate date = LocalDate.parse(workingDate, DateTimeFormatter.BASIC_ISO_DATE);
			Shift newShift  = new Shift(newShiftId,startTime,endTime);
			if(ShiftUtils.isValid(newShift, shiftService.findShifts()))
			{
				return employeeService.updateEmployeeShift(id,shiftId,date,newShift);
			}
			else
				throw new IllegalArgumentException("Invalid new shift - shift id and times do not agree");
		}
		catch(DateTimeParseException dtpe) {
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Time must be in HHmm format", null);
		}
		catch(IllegalArgumentException ie) {
			throw new ResponseStatusException(
					HttpStatus.FAILED_DEPENDENCY, ie.getMessage(), ie);
		}
		catch(Exception de) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
						"A shift with the same working day for the same employee already exists", de);
		}
	}
	@DeleteMapping( "/capstone/employees/{id}/shifts/{shiftId}")
	public int deleteEmployeeShift(
			@PathVariable("id") String id,
				@PathVariable("shiftId") int shiftId,
					@RequestParam("workingDate") String workingDate) {
		try {
			if(!Constants.EMP_ID_PATTERN_MATCH.test(id)) 
				throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Employee Id format required: EMP-XXXX", null); 
			if(shiftService.findShifts().stream().noneMatch(s->s.getShiftId()==shiftId))
				throw new IllegalArgumentException("Invalid shift Id");
			return employeeService.deleteEmployeeShift(id,shiftId,LocalDate.parse(workingDate, DateTimeFormatter.BASIC_ISO_DATE));
		}
		catch(DateTimeParseException dtpe) {
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Date must be in yyyyMMdd format", null);
		}
		catch(NonExistentEntityException neee) {
			throw new ResponseStatusException(
				HttpStatus.FAILED_DEPENDENCY, neee.getMessage(), neee);
		}
		catch(IllegalArgumentException ie) {
			throw new ResponseStatusException(
					HttpStatus.FAILED_DEPENDENCY, ie.getMessage(), ie);
		}
	}
	@GetMapping(path="/capstone/employees/shifts")
	public List<Employee> getAllEmployeesShifts(
			@RequestParam("startDate") String startDate,
				@RequestParam("endDate") String endDate) {

		try {
			LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
			LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.BASIC_ISO_DATE);
			log.info("{} startDate: {} endDate: {}", startDate, endDate);
			return employeeService.findAllEmployeesShifts(start, end);
		}
		catch(DateTimeParseException dtpe) {
			throw new ResponseStatusException(
					HttpStatus.EXPECTATION_FAILED, "Date must be in yyyyMMdd format", null);
		}
	}

	@GetMapping(path="/capstone/employees")
	public List<Employee> getEmployees(){
		
		List<Employee> employees =  employeeService.getEmployees();
		if(employees.isEmpty()) {
			throw new ResponseStatusException(
			          HttpStatus.NOT_FOUND, "No Employees Found", null);
		}
		return employees;
	}
	
	@GetMapping(path = "/add")
	public ModelAndView add() {
	ModelAndView mav = new ModelAndView("add");
	   return mav;
	}
	
	@GetMapping(path = "/clockin")
	public ModelAndView clockin() {
	ModelAndView mav = new ModelAndView("clockin");
	   return mav;
	}

	@GetMapping(path = "/clockout")
	public ModelAndView clockout() {
	ModelAndView mav = new ModelAndView("clockout");
	   return mav;
	}
	
	@GetMapping(path = "/reports")
	public ModelAndView reports() {
	ModelAndView mav = new ModelAndView("reports");
	   return mav;
	}
	
	@GetMapping(path = "/shifts")
	public ModelAndView shifts() {
	ModelAndView mav = new ModelAndView("shifts");
	   return mav;
	}
	
	@GetMapping(path = "/employees")
	public ModelAndView employees() {
	ModelAndView mav = new ModelAndView("employees");
	   return mav;
	}
	
	@GetMapping(path = "/aboutus")
	public ModelAndView aboutus() {
	ModelAndView mav = new ModelAndView("aboutus");
	   return mav;
	}
}

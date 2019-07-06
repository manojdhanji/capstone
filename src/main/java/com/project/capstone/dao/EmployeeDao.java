package com.project.capstone.dao;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.project.capstone.exception.AddEmployeeException;
import com.project.capstone.exception.ClockInException;
import com.project.capstone.exception.ClockOutException;
import com.project.capstone.exception.NonExistentEntityException;
import com.project.capstone.model.Employee;
import com.project.capstone.model.Shift;
import com.project.capstone.service.ShiftService;
import com.project.capstone.utils.DateTimeUtils;
@Repository("employeeDao")
public class EmployeeDao extends AbstractDao {
	
	private static final String GET_EMP_SHIFTS_SQL = 
			"select e.*, s.shift_id, s.clock_in_time, s.clock_out_time, s.working_date from emp e " +
			 	"left outer join emp_shift s on e.emp_id = s.emp_id where e.emp_id = ? and to_char(s.working_date, 'YYYY-MM-DD') = ?";
	private static final String GET_EMP_SHIFTS_FOR_GIVEN_DATES_SQL = 
			"select e.*, s.shift_id, s.clock_in_time, s.clock_out_time, s.working_date from emp e " +
			 	"left outer join emp_shift s on e.emp_id = s.emp_id where e.emp_id = ? and s.working_date between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD')";
	private static final String GET_EMP_SQL = "select e.* from EMP e where e.emp_id = ?";
	private static final String CLOCK_IN_DML = "insert into emp_shift (emp_id, shift_id, clock_in_time, working_date) values (?,?,?,?)";
	
	private static final String INSERT_EMP_DML = "insert into emp (emp_id, first_name, last_name, email) values (?,?,?,?)";
	private static final String CLOCK_OUT_DML = "update emp_shift s set s.clock_out_time = ? where s.emp_id = ? and s.shift_id = ? and s.working_date = ?";
	
	private static final String GET_ALL_EMP_SHIFTS_FOR_GIVEN_DATES_SQL = 
			"select s.*,e.first_name,e.last_name,e.email from emp_shift s inner join emp e on s.emp_id = e.emp_id where s.working_date between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD')";
	
	@Autowired
	@Qualifier("oracleDataSource")
    private DataSource oracleDataSource;
	@Autowired
	private ShiftService shiftService;
	
    @PostConstruct
    public void init() {
    	super.init(oracleDataSource);
    }
    @Transactional(transactionManager="oracleTransactionManager")
    public int insertEmployee(Employee e) {
    	if(Objects.nonNull(e)) {
    		return this.jdbcTemplate
    			.update(INSERT_EMP_DML, 
    						new Object[] {e.getEmpId(),e.getFirstName(),e.getLastName(),e.getEmail()}
    			);
    	}
    	throw new AddEmployeeException("Employee cannot be null");
    }
    
    @Transactional(transactionManager="oracleTransactionManager")
    public int updateEmployeeShift(String id, int shiftId, LocalDateTime workingDateTime) {
    	if(StringUtils.isNotBlank(id) &&
    			Objects.nonNull(workingDateTime) &&
    				shiftService.findShifts().stream().anyMatch(s->s.getShiftId()==shiftId)) {
    		final LocalDate ld = workingDateTime.toLocalDate();
    		Optional<Employee> optEmployee = findEmployeeShifts(id,ld);
    		if(optEmployee.isPresent()) {
    			LocalDate workingDate = workingDateTime.toLocalDate();
    			String clockOutTime = DateTimeUtils.getLocalTime(workingDateTime.toLocalTime());
    			Optional<Shift> optShift = optEmployee.get().getShifts(workingDate).stream().filter(s->s.getShiftId()==shiftId).findFirst();
    			if(optShift.isPresent()) {
    				if(optShift.get().getShiftEndTime()==null) {
    					return this.jdbcTemplate
    							.update(CLOCK_OUT_DML, 
    										new Object[] {clockOutTime, id, shiftId, DateTimeUtils.convertLocalDateToDate(workingDate)});
    				}
    				else {
    					log.error("{} already clocked out", id);
    					throw new ClockOutException(MessageFormat.format("{0} already clocked out", id));
    				}
    			}
    			else {
    				log.error("Not the right shift");
    				throw new ClockOutException("Not the right shift");
    			}
    		}
    		else {
    			log.error("{} not clocked in", id);
    			throw new ClockOutException(MessageFormat.format("{0} not clocked in", id));
    		}
    	}
    	throw new ClockOutException("Employee id, working date and shift required");
	}
    @Transactional(transactionManager="oracleTransactionManager")
    public int insertEmployeeShift(String id,int shiftId,LocalDateTime workingDateTime) {
    	if(StringUtils.isNotBlank(id) &&
    			Objects.nonNull(workingDateTime) &&
    				shiftService.findShifts().stream().anyMatch(s->s.getShiftId()==shiftId)) {
    		Optional<Employee> optEmployee = findEmployee(id);
    		if(optEmployee.isPresent()) {
    			Object[] args = new Object[] {id,shiftId,DateTimeUtils.getLocalTime(workingDateTime.toLocalTime()), DateTimeUtils.convertLocalDateToDate(workingDateTime.toLocalDate())};
    			return this.jdbcTemplate.update(CLOCK_IN_DML, args);
    		}
    		throw new NonExistentEntityException(MessageFormat.format("Employee with EMP_ID {0} not found", id));
    	}
    	throw new ClockInException("Employee id, working date and shift required");
    }
    @Transactional(transactionManager="oracleTransactionManager", readOnly=true)
    public Optional<Employee> findEmployee(String id) {
    	Optional<Employee> optEmployee = Optional.<Employee>empty();
    	try {
	    	if(StringUtils.isNotBlank(id)) {
	    		final Employee e  = new Employee();
				optEmployee = 
	    				Optional.<Employee>of(
							jdbcTemplate
							.queryForObject(
								GET_EMP_SQL, 
								new Object[] {id}, 
									(rs, rowNum) ->
										{
											e.setEmpId(rs.getString("emp_id"));
											e.setLastName(rs.getString("last_name"));
											e.setFirstName(rs.getString("first_name"));
											e.setEmail(rs.getString("email"));
											return e;	
										}
							)
					);
			}
    	} 
    	catch (EmptyResultDataAccessException e) {
    		log.info("{}",e.getMessage());
    	}
    	return optEmployee;
    }

    @Transactional(transactionManager="oracleTransactionManager", readOnly=true)
    public Optional<Employee> findEmployeeShifts(String id, LocalDate startDate, LocalDate endDate) {
    	try {
    		if(StringUtils.isNotBlank(id)) {
    			Optional<Employee> optEmployee = findEmployee(id);
    			if(optEmployee.isPresent()) {
					final Employee e  = optEmployee.get();
					final Object[] params = new Object[] {id,startDate.format(DateTimeFormatter.ISO_LOCAL_DATE), endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)};
	    				 return 
							jdbcTemplate
							.query(
								GET_EMP_SHIFTS_FOR_GIVEN_DATES_SQL, 
									params, 
										(rs, rowNum) ->
											{
												if(rowNum==1) {
													e.setEmpId(rs.getString("emp_id"));
													e.setLastName(rs.getString("last_name"));
													e.setFirstName(rs.getString("first_name"));
													e.setEmail(rs.getString("email"));
												}
												Shift s = new Shift();
												s.setShiftId(rs.getInt("shift_id"));
												s.setShiftStartTime(DateTimeUtils.getLocalTime(rs.getString("clock_in_time")));
												s.setShiftEndTime(DateTimeUtils.getLocalTime(rs.getString("clock_out_time")));
												e.addShift(DateTimeUtils.convertDateToLocalDate(rs.getDate("working_date")), s);
												return e;	
											}
						)
						.stream()
						.findFirst();
    			}
    		}
    		throw new NonExistentEntityException(MessageFormat.format("Employee with EMP_ID {0} not found", id));
		} catch (EmptyResultDataAccessException e) {
			return Optional.<Employee>empty();
		}			
    }
    
    
    
    @Transactional(transactionManager="oracleTransactionManager", readOnly=true)
    public Optional<Employee> findEmployeeShifts(String id, LocalDate workingDate) {
    	try {
    		if(StringUtils.isNotBlank(id)) {
    			Optional<Employee> optEmployee = findEmployee(id);
    			if(optEmployee.isPresent()) {
					final Employee e  = optEmployee.get();
	    				 return 
							jdbcTemplate
							.query(
								GET_EMP_SHIFTS_SQL, 
									new Object[] {id,workingDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}, 
										(rs, rowNum) ->
											{
												if(rowNum==1) {
													e.setEmpId(rs.getString("emp_id"));
													e.setLastName(rs.getString("last_name"));
													e.setFirstName(rs.getString("first_name"));
													e.setEmail(rs.getString("email"));
												}
												Shift s = new Shift();
												s.setShiftId(rs.getInt("shift_id"));
												s.setShiftStartTime(DateTimeUtils.getLocalTime(rs.getString("clock_in_time")));
												s.setShiftEndTime(DateTimeUtils.getLocalTime(rs.getString("clock_out_time")));
												e.addShift(workingDate, s);
												return e;	
											}
						)
						.stream()
						.findFirst();
    			}
    		}
    		throw new NonExistentEntityException(MessageFormat.format("Employee with EMP_ID {0} not found", id));
		} catch (EmptyResultDataAccessException e) {
			return Optional.<Employee>empty();
		}			
    }
 
    @Transactional(transactionManager="oracleTransactionManager", readOnly=true)
    public List<Employee> findEmployees() {
			return 
				jdbcTemplate
				.query("select * from EMP order by emp_id", 
						(rs, rowNum) ->
							{
								Employee e  = new Employee();
								e.setEmpId(rs.getString("emp_id"));
								e.setLastName(rs.getString("last_name"));
								e.setFirstName(rs.getString("first_name"));
								e.setEmail(rs.getString("email"));
								return e;	
							}
				);
    }
    
    private Optional<Employee> getEmployeeFromCache(List<Employee> employeeList,String id){
    	Optional<Employee> optEmployee = Optional.<Employee>empty();
    	if(!CollectionUtils.isEmpty(employeeList) &&
    			StringUtils.isNotBlank(id)) {
    		optEmployee = employeeList.stream().filter(e->e.getEmpId().equals(id)).findFirst();
    	}
    	return optEmployee;
    }
    @Transactional(transactionManager="oracleTransactionManager", readOnly=true)
    public List<Employee> findAllEmployeesShifts(LocalDate startDate, LocalDate endDate){
    	List<Employee> employeeList = new ArrayList<>();
    	return 
    		jdbcTemplate
    			.query(
    				GET_ALL_EMP_SHIFTS_FOR_GIVEN_DATES_SQL,  
    					new Object[] {startDate.format(DateTimeFormatter.ISO_LOCAL_DATE), 
    									endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)},
	    				rs->
	    				{
	    					while(rs.next()) {
	    						String id = rs.getString("emp_id");
	    						Employee emp = null;
	    						Optional<Employee> optEmployee = getEmployeeFromCache(employeeList,id);
	    						if(!optEmployee.isPresent()) {
	    							emp = new Employee();
	    							emp.setEmpId(id);
	    							emp.setFirstName(rs.getString("first_name"));
	    							emp.setLastName(rs.getString("last_name"));
									emp.setEmail(rs.getString("email"));
									employeeList.add(emp);
									optEmployee=getEmployeeFromCache(employeeList,id);
	    						}
	    						emp = optEmployee.get();
	    						LocalDate workingDate = DateTimeUtils.convertDateToLocalDate(rs.getDate("working_date"));
	    						Shift shift = new Shift();
	    						shift.setShiftId(rs.getInt("shift_id"));
	    						shift.setShiftStartTime(DateTimeUtils.getLocalTime(rs.getString("clock_in_time")));
								shift.setShiftEndTime(DateTimeUtils.getLocalTime(rs.getString("clock_out_time")));	    						
	    						emp.addShift(workingDate, shift);
	    					}
	    					return employeeList;
	    				}
    	);
    	
    	
    }
}

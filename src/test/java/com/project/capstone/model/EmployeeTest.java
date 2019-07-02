package com.project.capstone.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

import org.junit.Test;
public class EmployeeTest {
	
	@Test
	public void test1() {
		Employee e = new Employee();
		e.setEmail("manoj.dhanji@gmail.com");
		e.setEmpId("EMP-0001");
		e.setFirstName("Manoj");
		e.setLastName("Dhanji");
		
		Shift s1 = new Shift();
		s1.setShiftId(1);
		s1.setShiftStartTime(LocalTime.of(06, 00));
		s1.setShiftEndTime(LocalTime.of(14, 00));
		
		Shift s2 = new Shift();
		s2.setShiftId(2);
		s2.setShiftStartTime(LocalTime.of(14, 00));
		s2.setShiftEndTime(LocalTime.of(22, 00));
		
		
		e.addShift(LocalDate.of(2019,Month.JUNE, 19), s1);
		e.addShift(LocalDate.of(2019,Month.JUNE, 19), s2);
		e.getShifts(LocalDate.of(2019, Month.JUNE, 19)).stream().forEach(System.out::print);
		assertThat(e.getShifts(LocalDate.of(2019,Month.JUNE, 19)).size(), is(2));
		assertThat(e.getShifts(LocalDate.of(2019,Month.JUNE, 19)).contains(s1), is(true));
		assertThat(e.getShifts(LocalDate.of(2019,Month.JUNE, 19)).contains(s2), is(true));
	}
	
}

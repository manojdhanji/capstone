package com.project.capstone.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiftServiceTest {
	@Autowired
	private ShiftService shiftService;
	@Test
	public void test1() {
		assertThat(shiftService.findClosestShift().getShiftId(), is(3));
	}
}

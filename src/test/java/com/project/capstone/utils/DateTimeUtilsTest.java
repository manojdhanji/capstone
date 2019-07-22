package com.project.capstone.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class DateTimeUtilsTest {
	@Test
	public void test1() {
		assertThat(DateTimeUtils.getLocalTime(LocalTime.of(18, 35)), is("1835"));
	}
	@Test
	public void test2() {
		assertThat(DateTimeUtils.getLocalTime("1835"), is(LocalTime.of(18, 35)));
	}
	@Test
	public void test3() {
		LocalTime lt = LocalTime.parse("1259", DateTimeFormatter.ofPattern("HHmm"));
		System.out.println(lt);
	}
	
}

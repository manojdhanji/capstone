package com.project.capstone.utils;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public final class Constants {
	private Constants() {};
	public static final Pattern HHmm_REGEX = Pattern.compile("^[\\d]{4}$");
	public static final Pattern yyyyMMdd_REGEX = Pattern.compile("^[\\d]{8}$");
	public static final Pattern EMP_ID_REGEX = Pattern.compile("^EMP-[\\d]{4}$");
	public static final String PASSWORD = "password";
	public static final String MASKED_PASSWORD = "****";
	public static final Pattern EMAIL_REGEX = Pattern.compile( "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
	public static final Predicate<String> EMP_ID_PATTERN_MATCH =
			empId->	StringUtils.isNotBlank(empId) && Constants.EMP_ID_REGEX.matcher(empId).matches();
	public static final Predicate<String> EMAIL_PATTERN_MATCH =
			email->	StringUtils.isNotBlank(email) && Constants.EMAIL_REGEX.matcher(email).matches();

}

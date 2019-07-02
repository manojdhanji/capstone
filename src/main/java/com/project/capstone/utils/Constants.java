package com.project.capstone.utils;

import java.util.regex.Pattern;

public final class Constants {
	private Constants() {};
	public static final Pattern HHmm_REGEX = Pattern.compile("^[\\d]{4}$");
	public static final Pattern yyyyMMdd_REGEX = Pattern.compile("^[\\d]{8}$");
	public static final Pattern EMP_ID_REGEX = Pattern.compile("^EMP-[\\d]{4}$");
	public static final String PASSWORD = "password";
	public static final String MASKED_PASSWORD = "****";
}

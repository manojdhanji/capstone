package com.project.capstone.utils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public final class DateTimeUtils {
	private DateTimeUtils() {}
	public static boolean isInBetween(LocalTime start, LocalTime end, LocalTime time) {
		  if (start.isAfter(end)) {
		    return !time.isBefore(start) || !time.isAfter(end);
		  } else {
		    return !time.isBefore(start) && !time.isAfter(end);
		  }
		}
	public static LocalTime getLocalTime(String s) {
		if(StringUtils.isNotBlank(s) && 
				Constants.HHmm_REGEX.matcher(s).matches()) {
			return LocalTime.of(Integer.parseInt(s.substring(0, 2)), Integer.parseInt(s.substring(2)));
		}
		return null;
	}
	
	public static String getLocalTime(LocalTime lt) {
		if(Objects.nonNull(lt)) {
			return StringUtils.join(String.format("%02d", lt.getHour()), 
					String.format("%02d", lt.getMinute()));
			 
		}
		return null;
	}
	public static Date convertLocalDateToDate(LocalDate localDate) {
		if(localDate==null)
			return null;
		return Date.valueOf(localDate);
	}
	public static LocalDate convertDateToLocalDate(Date date) {
		if(date==null)
			return null;
		return date.toLocalDate();
	}
}

package com.project.capstone.utils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.util.CollectionUtils;

import com.project.capstone.model.Shift;

public final class ShiftUtils {
	private ShiftUtils() {}
	
	public static boolean isValid(Shift newShift, List<Shift> shifts) {
		if(Objects.nonNull(newShift) &&
				!CollectionUtils.isEmpty(shifts)) {
			Optional<Shift> optShift = shifts.stream().filter(s->s.getShiftId()==newShift.getShiftId()).findFirst();
			if(optShift.isPresent()) {
				Shift shift = optShift.get();
				return DateTimeUtils.isInBetween(shift.getShiftStartTime(), shift.getShiftEndTime(), newShift.getShiftStartTime()) /*&&
						DateTimeUtils.isInBetween(shift.getShiftStartTime(), shift.getShiftEndTime(), newShift.getShiftEndTime())*/;
			}
		}
		return false;
	}
}

package com.project.capstone.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.project.capstone.dao.ShiftDao;
import com.project.capstone.model.Shift;

@Service
@CacheConfig(cacheNames = {"shifts"})
public class ShiftService {
	@Autowired
	@Qualifier("shiftDao")
	private ShiftDao shiftDao;

	@Cacheable	
	public List<Shift> findShifts(){
		return shiftDao.findShifts();
	}
}

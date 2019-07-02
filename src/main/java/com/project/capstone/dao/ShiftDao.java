package com.project.capstone.dao;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.capstone.model.Shift;
import com.project.capstone.utils.DateTimeUtils;

@Repository("shiftDao")
public class ShiftDao extends AbstractDao{
	private static final String GET_SHIFT_SQL = "select s.* from shift s";
	@Autowired
	@Qualifier("oracleDataSource")
    private DataSource oracleDataSource;
	
    @PostConstruct
    public void init() {
    	super.init(oracleDataSource);
    }
    @Transactional(transactionManager="oracleTransactionManager", readOnly=true)
    public List<Shift> findShifts() {
    	return this.jdbcTemplate
    				.query(GET_SHIFT_SQL, 
    						(rs, rowNum)->
    						{
    							Shift s = new Shift();
    							s.setShiftId(rs.getInt("shift_id"));
								s.setShiftStartTime(DateTimeUtils.getLocalTime(rs.getString("shift_start_time")));
								s.setShiftEndTime(DateTimeUtils.getLocalTime(rs.getString("shift_end_time")));
    							return s;
    						}
    	);
    }
}

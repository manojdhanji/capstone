package com.project.capstone.dao;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractDao {
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	protected JdbcTemplate jdbcTemplate;
	protected void init(DataSource dataSource ) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try (Connection conn = jdbcTemplate.getDataSource().getConnection()){
			if(log.isDebugEnabled()) {log.debug(conn.getSchema());}
		} 
		catch (SQLException e) {
			log.error("Failed to get a connection for datasource {}",e.getMessage());
		}
	
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
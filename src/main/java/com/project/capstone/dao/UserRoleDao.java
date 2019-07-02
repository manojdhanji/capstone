package com.project.capstone.dao;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.capstone.model.UserRole;
@Repository("userRoleDao")
public class UserRoleDao extends AbstractDao {
	@Autowired
	@Qualifier("oracleDataSource")
    private DataSource oracleDataSource;
	private static final String INSERT_ROLE_DML = "insert into user_roles (username, role) values(?,?)";
	
    @PostConstruct
    public void init() {
    	super.init(oracleDataSource);
    }
    public int insertRole(String userName) {
    	return insertRole(userName,Optional.<Set<UserRole>>empty());
    }
	@Transactional(transactionManager="oracleTransactionManager")
	public int insertRole(String userName, Optional<Set<UserRole>> optRoles) {
		int rows = 0;
		if(StringUtils.isNotBlank(userName) &&
				Objects.nonNull(optRoles)) {
			Set<UserRole> roles = new HashSet<>();
			if(optRoles.isPresent()) {
				roles = optRoles.get();
			}
			else
				roles.add(UserRole.ROLE_USER);
			Object[] args = new Object[2];
			args[0] = userName;
			for(UserRole r: roles) {
				try {
					args[1] = r.toString();
					this.jdbcTemplate.update(INSERT_ROLE_DML ,args);
				}
				catch(DataAccessException e) {
					log.error("{}", e.getMostSpecificCause().getMessage());
				}
			}
		}
		return rows;
	}
}

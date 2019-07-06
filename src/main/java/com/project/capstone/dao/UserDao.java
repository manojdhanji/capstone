package com.project.capstone.dao;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.capstone.model.User;
import com.project.capstone.model.UserRole;
import com.project.capstone.utils.Constants;

@Repository("userDao")
public class UserDao extends AbstractDao {
	private static final String INSERT_USER_SQL = "insert into users (username, password) values (?,?)";
	private static final String UPDATE_USER_SQL = "update users set password = ?, enabled = ? where username = ?";
	private static final String GET_USER = "select u.*,r.role from users u left outer join user_roles r on u.username = r.username"; 
	/*@Autowired
	private PasswordEncoder passwordEncoder;*/
	
	@Autowired
	@Qualifier("oracleDataSource")
    private DataSource oracleDataSource;
	
    @PostConstruct
    public void init() {
    	super.init(oracleDataSource);
    }
	
	@Transactional(transactionManager="oracleTransactionManager")
	public int insertUser(String userName, Optional<String> optPassword) {
		int rows = 0;
		if(StringUtils.isNotBlank(userName)) {
			String password = "";//passwordEncoder.encode(optPassword.isPresent()?optPassword.get():Constants.PASSWORD);
			try {
				rows = this.jdbcTemplate
							.update(INSERT_USER_SQL, 
										new Object[] {userName,password});
			}
			catch(DataIntegrityViolationException e) {
				log.error(e.getMostSpecificCause().getMessage());
			}
		}
		return rows;
	}
	public int updateUser(String userName, String password) {
		return updateUser(userName,password,true);
	}
	
	@Transactional(transactionManager="oracleTransactionManager")
	public int updateUser(String userName, String password, boolean enabled) {
		int rows = 0;
		if(StringUtils.isNotBlank(userName)) {
			try {
				rows = this.jdbcTemplate
							.update(UPDATE_USER_SQL, 
										new Object[] {/*passwordEncoder.encode(password)*/password, enabled?1:0, userName});
			}
			catch(DataIntegrityViolationException e) {
				log.error(e.getMostSpecificCause().getMessage());
			}
		}
		return rows;
	}
	
	@Transactional(transactionManager="oracleTransactionManager", readOnly=true)
	public Optional<User> getUser(String userName) {
		Optional<User> optUser = Optional.<User>empty();
		User u = new User();
		if(StringUtils.isNotBlank(userName)) {
			AtomicInteger count = new AtomicInteger();
			count.set(0);
			optUser = Optional.<User>of(
				this.jdbcTemplate
					.query(GET_USER, 
							new Object[] {userName}, 
							rs->{
								while(rs.next()) {
									if(count.getAndIncrement()==1) {
										u.setEnabled(rs.getInt("enabled")==1?true:false);
										u.setPassword(Constants.MASKED_PASSWORD);
										u.setUserName(rs.getString("username"));
									}
									u.addRole(UserRole.valueOf(rs.getString("role")));
								}
								return u;
							}
					)
			);
		}
		return optUser;
	}
}

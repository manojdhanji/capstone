package com.project.capstone.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.capstone.dao.UserDao;
import com.project.capstone.model.User;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;
	
	public Optional<User> getUser(String userName) {
		return userDao.getUser(userName);
	}
	public int insertUser(User user) {
		String userName  = user.getUserName();
		Optional<String> optPassword = Optional.<String>ofNullable(user.getPassword());
		return userDao.insertUser(userName, optPassword);
	}
	public int updateUser(User user) {
		return userDao.updateUser(user.getUserName(), user.getPassword(), user.isEnabled());
	}
}

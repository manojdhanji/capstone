package com.project.capstone.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User implements Serializable {
	static final long serialVersionUID = -1472327710395474550L;
	private String userName;
	private String password;
	private boolean enabled;
	private final Set<UserRole> roles = new HashSet<>();
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Set<UserRole> getRoles() {
		return roles;
	}
	public void addRole(UserRole role) {
		if(Objects.nonNull(role))
			this.roles.add(role);
	}
	@Override
	public String toString() {
		return "User [userName=" + userName + ", password=" + password + ", enabled=" + enabled + ", roles=" + roles
				+ "]";
	}
	
}

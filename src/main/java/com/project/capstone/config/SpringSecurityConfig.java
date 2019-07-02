package com.project.capstone.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource oracleDataSource;
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
	   auth.jdbcAuthentication().dataSource(oracleDataSource)
	  .usersByUsernameQuery(
	   "select username,password, enabled from users where username=?")
	  .authoritiesByUsernameQuery(
	   "select username, role from user_roles where username=?").passwordEncoder(new BCryptPasswordEncoder());
	 } 
    
	/*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("EMP-0002").password("{noop}password").roles("USER")
            .and()
            .withUser("EMP-0003").password("{noop}password").roles("USER")
            .and()
            .withUser("EMP-0001").password("{noop}password").roles("USER", "ADMIN");
    }*/
    
	
	// Secure the endpoints with HTTP Basic authentication
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            //HTTP Basic authentication
            .httpBasic()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.PATCH, "/capstone/employee/clockout/*").hasAnyRole("ADMIN","USER")
            .antMatchers(HttpMethod.POST, "/capstone/employee/clockin/*").hasAnyRole("ADMIN","USER")
            .antMatchers(HttpMethod.POST, "/capstone/employees/add").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/capstone/employee/*").hasAnyRole("ADMIN","USER")
            .antMatchers(HttpMethod.GET, "/capstone/employees").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/capstone/shifts").hasAnyRole("ADMIN","USER")
            .and()
            /*.csrf()
            .and()*/
            .formLogin()
            ;
    }

    /*@Bean
    public UserDetailsService userDetailsService() {
        //ok for demo
        User.UserBuilder users = User.withDefaultPasswordEncoder();

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(users.username("user").password("password").roles("USER").build());
        manager.createUser(users.username("admin").password("password").roles("USER", "ADMIN").build());
        return manager;
    }*/
}
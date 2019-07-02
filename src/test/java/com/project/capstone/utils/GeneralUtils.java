package com.project.capstone.utils;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneralUtils {
	@Test
	public void test() {
		System.out.println(new BCryptPasswordEncoder().encode("password"));
	}
}

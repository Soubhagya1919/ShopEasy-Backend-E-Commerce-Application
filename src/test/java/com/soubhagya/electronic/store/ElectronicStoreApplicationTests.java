package com.soubhagya.electronic.store;

import com.soubhagya.electronic.store.entities.User;
import com.soubhagya.electronic.store.repositories.UserRepository;
import com.soubhagya.electronic.store.security.JwtHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ElectronicStoreApplicationTests {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtHelper jwtHelper;

	@Test
	void contextLoads() {
	}

	@Test
	void testToken(){
		User user = userRepository.findByEmail("soubhagya@gmail.com").get();
		//here User is UserDetail itself

		String token = jwtHelper.generateToken(user);

		System.out.println("Testing jwt tokens");
		System.out.println(token);

		System.out.println("getting username from token");
		System.out.println(jwtHelper.getUsernameFromToken(token));

		System.out.println("Is token expired: " + jwtHelper.isTokenExpired(token));
	}

}

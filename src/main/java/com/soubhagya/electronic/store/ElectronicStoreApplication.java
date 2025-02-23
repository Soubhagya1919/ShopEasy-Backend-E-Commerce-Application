package com.soubhagya.electronic.store;

import com.soubhagya.electronic.store.constants.AppConstants;
import com.soubhagya.electronic.store.entities.Role;
import com.soubhagya.electronic.store.entities.User;
import com.soubhagya.electronic.store.repositories.RoleRepository;
import com.soubhagya.electronic.store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
@EnableWebMvc
public class ElectronicStoreApplication implements CommandLineRunner {
//As soon as we launch the application we want two roles to be ready ADMIN and NORMAL

	private final RoleRepository roleRepository;

	@Value("${normal.role.id}")
	private String role_normal_id;
	@Value("${admin.role.id}")
	private String role_admin_id;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public ElectronicStoreApplication(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public static void main(String[] args) {
		SpringApplication.run(ElectronicStoreApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//--old
//		Role roleAdmin = roleRepository.findByName("ROLE_" + AppConstants.ROLE_ADMIN).orElse(null);
//
//		if(roleAdmin == null) {
//			roleAdmin = new Role();
//			roleAdmin.setRoleId(UUID.randomUUID().toString());
//			roleAdmin.setName("ROLE_" + AppConstants.ROLE_ADMIN);
//		}
//
//		Role roleNormal = roleRepository.findByName("ROLE_" + AppConstants.ROLE_NORMAL).orElse(null);
//
//		if(roleNormal == null) {
//			roleNormal = new Role();
//			roleNormal.setRoleId(UUID.randomUUID().toString());
//			roleNormal.setName("ROLE_" + AppConstants.ROLE_NORMAL);
//		}
//
//		roleRepository.saveAll(List.of(roleAdmin, roleNormal));
//
//		//make an user admin
//		User user = userRepository.findByEmail("soubhagya@gmail.com").orElse(null);
//		if(user == null) {
//			user = new User();
//			user.setName("Soubhagya");
//			user.setEmail("soubhagya@gmail.com");
//			user.setPassword(passwordEncoder.encode("Soubhagya"));
//			user.setRoles(List.of(roleAdmin));
//			user.setUserId(UUID.randomUUID().toString());
//			userRepository.save(user);
//		}
		//--new
		System.out.println(passwordEncoder.encode("abcd"));

		try {

			Role role_admin = roleRepository.findByName("ROLE_" + AppConstants.ROLE_ADMIN).orElse(null);

			if(role_admin == null) {

				role_admin = new Role();
				role_admin.setRoleId(role_admin_id);
				role_admin.setName("ROLE_" + AppConstants.ROLE_ADMIN);

			}

			Role role_normal = roleRepository.findByName("ROLE_" + AppConstants.ROLE_NORMAL).orElse(null);

			if(role_normal == null) {

				role_normal = new Role();
				role_normal.setRoleId(role_normal_id);
				role_normal.setName("ROLE_" + AppConstants.ROLE_NORMAL);

			}

			roleRepository.saveAll(List.of(role_admin, role_normal));

			User adminUser = User.builder()
					.name("admin")
					.email("admin@gmail.com")
					.password(passwordEncoder.encode("admin123"))
					.gender("Male")
					.imageName("default.png")
					.roles(Set.of(role_admin, role_normal))
					.userId(UUID.randomUUID().toString())
					.about("I am admin User")
					.build();

			User normalUser = userRepository.findByEmail("soubhagya@gmail.com").orElse(null);

			if(normalUser == null) {

				normalUser = User.builder()
						.name("Soubhagya")
						.email("soubhagya@gmail.com")
						.password(passwordEncoder.encode("soubhagya123"))
						.gender("Male")
						.imageName("soubhagya.png")
						.userId(UUID.randomUUID().toString())
						.about("I am Normal User")
						.build();
			}
			normalUser.setRoles(Set.of(role_normal));

			userRepository.save(adminUser);
			userRepository.save(normalUser);

		} catch (Exception e) {
//            e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}
}

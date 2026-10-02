package auth.auth_app_backend;

import auth.auth_app_backend.auth.config.ApiConstants;
import auth.auth_app_backend.auth.entities.Role;
import auth.auth_app_backend.auth.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class AuthAppBackendApplication implements CommandLineRunner {
@Autowired
private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(AuthAppBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	//we will create some default user role
	//GUEST
	//admin
		Role role = new Role();
		roleRepository.findByName("ROLE_"+ApiConstants.ADMIN_ROLE).ifPresentOrElse( newrole->{
			System.out.println("Admin Role Exists"+newrole.getName());
				},
				()->{
			role.setName("ROLE_"+ApiConstants.ADMIN_ROLE);
			role.setId(UUID.randomUUID());
			roleRepository.save(role);

		});

		roleRepository.findByName("ROLE_"+ApiConstants.GUEST_ROLE).ifPresentOrElse( newrole->{
					System.out.println("Guest Role Exists"+newrole.getName());
				},
				()->{
			role.setName("ROLE_"+ApiConstants.GUEST_ROLE);
			role.setId(UUID.randomUUID());
			roleRepository.save(role);

		});
	}
}

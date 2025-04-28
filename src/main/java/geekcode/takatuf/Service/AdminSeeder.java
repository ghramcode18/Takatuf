package geekcode.takatuf.Service;


import geekcode.takatuf.Entity.Role;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Enums.RoleName;
import geekcode.takatuf.Enums.UserType;
import geekcode.takatuf.Repository.RoleRepository;
import geekcode.takatuf.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminSeeder {
    @Autowired
    PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public AdminSeeder(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void seedAdminUser() {
        // Check if the admin user already exists
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            // Create a new admin user
            User adminUser = User.builder()
                    .name("admin")
                    .email("admin@example.com")
                    .phoneNumber("0946")
                    .password("admin123")
                    .password(passwordEncoder.encode("admin123"))
                    .createdAt(LocalDateTime.now())
                    .type(UserType.ADMIN)
                    .build();

            // Save the admin user to the database
            userRepository.save(adminUser);
            System.out.println("Admin user created: username=admin, password=admin123");
        } else {
            System.out.println("Admin user already exists.");
        }
    }
    @PostConstruct
    public void seedRole() {
        if (roleRepository.findByRoleName(RoleName.ADMIN).isEmpty()) {
            Role role = Role.builder()
                    .roleName(RoleName.ADMIN)
                    .build();
            roleRepository.save(role);
            System.out.println("ADMIN role created successfully");
        } else {
            System.out.println("ADMIN Role already exists.");
        }
        if (roleRepository.findByRoleName(RoleName.USER).isEmpty()) {
        Role role2 = Role.builder()
                .roleName(RoleName.USER)
                .build();
        roleRepository.save(role2);
            System.out.println("USER role created successfully");
        } else {
            System.out.println("USER Role already exists.");
        }
    }
}

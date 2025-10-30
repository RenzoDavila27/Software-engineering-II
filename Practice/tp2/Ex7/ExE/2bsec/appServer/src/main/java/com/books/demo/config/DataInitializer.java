package com.books.demo.config;

import com.books.demo.auth.entity.Role;
import com.books.demo.auth.entity.User;
import com.books.demo.auth.enums.RoleName;
import com.books.demo.auth.repository.RoleRepository;
import com.books.demo.auth.repository.UserRepository;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN)));
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_USER)));

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@books.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRoles(Set.of(adminRole, userRole));
            userRepository.save(admin);
            log.info("Usuario administrador por defecto creado: admin/Admin123!");
        }

        userRepository.findByUsername("ramiro").ifPresent(u -> {
            boolean matches = passwordEncoder.matches("ramiro", u.getPassword());
            log.info("Password check for ramiro -> {}", matches);
        });
    }
}

package com.books.demo.auth.service;

import com.books.demo.auth.dto.RegisterRequest;
import com.books.demo.auth.entity.Role;
import com.books.demo.auth.entity.User;
import com.books.demo.auth.enums.RoleName;
import com.books.demo.auth.repository.RoleRepository;
import com.books.demo.auth.repository.UserRepository;
import java.util.Locale;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String normalizedUsername = username.toLowerCase(Locale.ROOT);
        String email = request.getEmail().trim();
        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Rol USER no configurado"));

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
    }
}

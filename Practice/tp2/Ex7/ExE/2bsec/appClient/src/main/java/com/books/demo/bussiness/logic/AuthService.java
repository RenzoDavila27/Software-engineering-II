package com.books.demo.bussiness.logic;

import com.books.demo.client.dto.JwtResponse;
import com.books.demo.client.dto.LoginRequest;
import com.books.demo.client.dto.RegisterRequest;
import com.books.demo.client.exception.ApiClientException;
import com.books.demo.repository.AuthRepository;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    public static final String TOKEN_SESSION_ATTRIBUTE = "jwtToken";
    public static final String ROLES_SESSION_ATTRIBUTE = "userRoles";
    public static final String USERNAME_SESSION_ATTRIBUTE = "username";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void login(LoginRequest loginRequest, HttpSession session) {
        try {
            loginRequest.setUsername(loginRequest.getUsername().trim());
            JwtResponse response = authRepository.login(loginRequest);
            if (response == null || !StringUtils.hasText(response.getToken())) {
                throw new IllegalArgumentException("La respuesta del servicio de autenticación es inválida.");
            }
            session.setAttribute(TOKEN_SESSION_ATTRIBUTE, response.getToken());
            session.setAttribute(ROLES_SESSION_ATTRIBUTE, response.getRoles() != null
                    ? response.getRoles()
                    : Collections.emptyList());
            session.setAttribute(USERNAME_SESSION_ATTRIBUTE, response.getUsername());
        } catch (ApiClientException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    public void register(RegisterRequest registerRequest) {
        try {
            registerRequest.setUsername(registerRequest.getUsername().trim());
            registerRequest.setEmail(registerRequest.getEmail().trim());
            authRepository.register(registerRequest);
        } catch (ApiClientException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public boolean isAuthenticated(HttpSession session) {
        return session != null && session.getAttribute(TOKEN_SESSION_ATTRIBUTE) != null;
    }

    @SuppressWarnings("unchecked")
    public boolean hasRole(HttpSession session, String role) {
        if (!isAuthenticated(session) || !StringUtils.hasText(role)) {
            return false;
        }
        Object rolesAttribute = session.getAttribute(ROLES_SESSION_ATTRIBUTE);
        if (rolesAttribute instanceof List<?> roles) {
            return roles.stream()
                    .map(Object::toString)
                    .anyMatch(r -> r.equalsIgnoreCase(role));
        }
        return false;
    }

    public String getUsername(HttpSession session) {
        if (!isAuthenticated(session)) {
            return null;
        }
        Object username = session.getAttribute(USERNAME_SESSION_ATTRIBUTE);
        return username != null ? username.toString() : null;
    }

    public String getToken(HttpSession session) {
        if (!isAuthenticated(session)) {
            return null;
        }
        Object token = session.getAttribute(TOKEN_SESSION_ATTRIBUTE);
        return token != null ? token.toString() : null;
    }
}

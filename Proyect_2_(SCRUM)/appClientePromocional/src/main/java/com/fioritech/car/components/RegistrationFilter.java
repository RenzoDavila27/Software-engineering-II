package com.fioritech.car.components;

import com.fioritech.car.bussiness.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class RegistrationFilter extends OncePerRequestFilter {

    private final UsuarioService usuarioService;

    private final List<String> allowedPaths = List.of(
            "/login", "/register", "/exitAccess",
            "/css", "/js", "/img", "/lib", "/api", "/"
    );

    @Autowired
    public RegistrationFilter(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Si la ruta es pública, dejar pasar
        boolean isAllowed = allowedPaths.stream().anyMatch(path::startsWith);
        if (isAllowed) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si no hay autenticación, dejar pasar (Spring Security lo bloqueará)
        if (authentication == null || !(authentication instanceof OAuth2AuthenticationToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = usuarioService.getEmailFromAuthentication(authentication);

        try {
            // Comprobar si el usuario existe.
            usuarioService.processUserLogin(email).block();
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // El usuario no existe
            response.sendRedirect("/register");
        }
    }
}
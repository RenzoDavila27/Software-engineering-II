package com.books.demo.config;

import com.books.demo.bussiness.logic.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Set<String> PUBLIC_PATTERNS = Set.of(
            "/auth/**",
            "/",
            "/css/**",
            "/js/**",
            "/images/**",
            "/icon/**",
            "/fonts/**",
            "/favicon.ico"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String requestUri = request.getRequestURI();
        if (isPublicPath(requestUri)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (!authService.isAuthenticated(session)) {
            response.sendRedirect("/auth/login");
            return false;
        }

        if (requiresAdmin(requestUri, request.getMethod()) && !authService.hasRole(session, AuthService.ROLE_ADMIN)) {
            response.sendRedirect("/libros");
            return false;
        }

        return true;
    }

    private boolean isPublicPath(String requestUri) {
        return PUBLIC_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }

    private boolean requiresAdmin(String requestUri, String method) {
        if (requestUri.startsWith("/autores") || requestUri.startsWith("/usuarios")) {
            return true;
        }
        if (requestUri.startsWith("/libros")) {
            String normalized = requestUri;
            if (normalized.endsWith("/") && normalized.length() > 1) {
                normalized = normalized.substring(0, normalized.length() - 1);
            }
            boolean isListingRequest = "/libros".equals(normalized) && HttpMethod.GET.matches(method);
            return !isListingRequest;
        }
        return false;
    }
}


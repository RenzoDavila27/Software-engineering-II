package com.car.clientead.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class RoleBasedAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private static final String ROLE_ADMIN = "ROLE_ADMINISTRATIVO";
    private static final String ROLE_MANAGER = "ROLE_JEFE";
    private static final String ROLE_CLIENT = "ROLE_CLIENTE";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = resolveTarget(authentication.getAuthorities());
        setAlwaysUseDefaultTargetUrl(true);
        setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String resolveTarget(Collection<? extends GrantedAuthority> authorities) {
        boolean isAdmin = hasAuthority(authorities, ROLE_ADMIN) || hasAuthority(authorities, ROLE_MANAGER);
        if (isAdmin) {
            return "/";
        }
        if (hasAuthority(authorities, ROLE_CLIENT)) {
            return "/alquileres/historial";
        }
        return "/";
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities.stream().anyMatch(auth -> role.equals(auth.getAuthority()));
    }
}

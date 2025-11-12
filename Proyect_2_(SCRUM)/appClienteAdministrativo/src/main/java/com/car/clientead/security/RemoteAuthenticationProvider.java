package com.car.clientead.security;

import com.car.clientead.client.auth.RemoteAuthClient;
import com.car.clientead.client.auth.RemoteAuthenticationException;
import com.car.clientead.client.auth.dto.JwtResponse;
import com.car.clientead.client.dto.enums.RolUsuario;
import com.car.clientead.web.session.UserSession;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class RemoteAuthenticationProvider implements AuthenticationProvider {

    private static final String ROLE_PREFIX = "ROLE_";

    private final RemoteAuthClient authClient;
    private final UserSession userSession;

    public RemoteAuthenticationProvider(RemoteAuthClient authClient, UserSession userSession) {
        this.authClient = authClient;
        this.userSession = userSession;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = Objects.toString(authentication.getCredentials(), "");

        JwtResponse response;
        try {
            response = authClient.login(username, password);
        } catch (RemoteAuthenticationException ex) {
            throw new BadCredentialsException(ex.getMessage(), ex);
        }

        if (response.getAccessToken() == null) {
            throw new BadCredentialsException("No fue posible obtener un token de acceso");
        }

        List<String> roles = response.getRoles();
        List<SimpleGrantedAuthority> authorities = roles == null ? List.of()
                : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        RemoteJwtPrincipal principal = new RemoteJwtPrincipal(
                username,
                response.getAccessToken(),
                response.getRefreshToken(),
                Instant.now().plusSeconds(Math.max(response.getExpiresIn(), 0)),
                roles
        );

        userSession.actualizar(resolverRol(roles), null);

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private RolUsuario resolverRol(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return RolUsuario.ADMINISTRATIVO;
        }
        String raw = roles.get(0);
        String clean = raw != null && raw.startsWith(ROLE_PREFIX)
                ? raw.substring(ROLE_PREFIX.length())
                : raw;
        try {
            return RolUsuario.valueOf(clean.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return RolUsuario.ADMINISTRATIVO;
        }
    }
}

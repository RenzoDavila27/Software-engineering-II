package com.car.seguridad.authentication;

import com.car.business.domain.Usuario;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final String id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public static UserPrincipal fromUsuario(Usuario usuario) {
        List<SimpleGrantedAuthority> grantedAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRolUsuario().name())
        );
        boolean activo = !Boolean.TRUE.equals(usuario.getEliminado());
        return new UserPrincipal(
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getClave(),
                grantedAuthorities,
                activo
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

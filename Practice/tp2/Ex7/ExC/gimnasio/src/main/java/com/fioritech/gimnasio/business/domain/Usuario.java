package com.fioritech.gimnasio.business.domain;

import com.fioritech.gimnasio.business.domain.enums.RolUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority; // Importado para UserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importado para UserDetails
import org.springframework.security.core.userdetails.UserDetails; // Importado para UserDetails

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuarios")

public class Usuario extends BaseEntity implements UserDetails {

    @Column(name = "nombre_usuario", nullable = false, unique = true)
    private String nombreUsuario;

    @Column(nullable = false)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;


    @OneToMany(mappedBy = "usuario")
    private List<Promocion> promociones = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<Mensaje> mensajes = new ArrayList<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte nuestro 'RolUsuario' a un formato que Spring Security entiende
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public String getPassword() {
        // Devuelve el campo que usamos para la contraseña
        return clave;
    }

    @Override
    public String getUsername() {
        // Devuelve el campo que usamos como nombre de usuario
        return nombreUsuario;
    }

    // Dejamos estos como 'true' por defecto.
    // Si tu BaseEntity tiene una fecha de baja, podrías usarla en isEnabled().

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
        // Ejemplo: si BaseEntity tiene 'getFechaBaja()', podrías usar:
        // return this.getFechaBaja() == null;
        return true;
    }
}

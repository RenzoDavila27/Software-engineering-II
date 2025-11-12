package com.car.clientead.web.session;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.car.clientead.client.dto.enums.RolUsuario;

/**
 * Mantiene en sesión el contexto mínimo del usuario autenticado para poder
 * determinar qué vistas y funcionalidades mostrar.
 * <p>
 * Actualmente se inicializa como ADMINISTRATIVO, pero debe ser actualizado
 * por el flujo de autenticación una vez que se conozca el rol real del usuario.
 */
@Component
@SessionScope
public class UserSession {

    private RolUsuario rolActual = RolUsuario.ADMINISTRATIVO;
    private String clienteId;

    public RolUsuario getRolActual() {
        return rolActual;
    }

    public void setRolActual(RolUsuario rolActual) {
        this.rolActual = rolActual != null ? rolActual : RolUsuario.ADMINISTRATIVO;
        if (this.rolActual != RolUsuario.CLIENTE) {
            this.clienteId = null;
        }
    }

    public Optional<String> getClienteId() {
        return Optional.ofNullable(clienteId);
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public void actualizar(RolUsuario rol, String clienteIdAsociado) {
        setRolActual(rol);
        if (rol == RolUsuario.CLIENTE) {
            this.clienteId = clienteIdAsociado;
        }
    }
}


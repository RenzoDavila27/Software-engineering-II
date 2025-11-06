package com.contactos.business.logic.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contactos.business.domain.Usuario;
import com.contactos.business.domain.enumeration.Rol;
import com.contactos.business.logic.error.ErrorServiceException;

@Service
public class InicioAplicacionService {

    private final UsuarioService usuarioService;

    public InicioAplicacionService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Transactional
    public void iniciarAplicacion() throws ErrorServiceException {
        if (usuarioService.buscarPorCuenta("administrador").isEmpty()) {
            Usuario administrador = new Usuario();
            administrador.setCuenta("administrador");
            administrador.setClave("123456");
            administrador.setRol(Rol.ADMIN);
            usuarioService.alta(administrador);
        }
    }
}

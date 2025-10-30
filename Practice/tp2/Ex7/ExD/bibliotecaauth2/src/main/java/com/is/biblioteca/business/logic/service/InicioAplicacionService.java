package com.is.biblioteca.business.logic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.is.biblioteca.business.logic.error.ErrorServiceException;

@Service
public class InicioAplicacionService {

	@Autowired
	private UsuarioService usuarioService;
	
	public void iniciarAplicacion() throws ErrorServiceException {
		
		try {
			
			var adminExistente = usuarioService.buscarUsuarioPorEmail("administrador@administrador");
			if (adminExistente == null) {
				var admin = usuarioService.crearUsuario("Administrador", "administrador@administrador", "1234567", "1234567", null);
				usuarioService.cambiarRol(admin.getId());
			}
			
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
	}
}

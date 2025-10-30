package com.example.mecanic.bussines.logic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mecanic.bussines.logic.error.ErrorServiceException;
import com.example.mecanic.bussines.domain.enumeration.Rol;

@Service
public class inicioService {

	@Autowired
	private UsuarioService usuarioService;
	
	public void crearUserDefault() throws ErrorServiceException {
		
		try {
			
			var adminExistente = usuarioService.buscarUsuarioPorNombre("administrador");
			if (adminExistente == null) {
				usuarioService.alta("Administrador", "1234567", "1234567",Rol.ADMIN);
			}
			
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
	}
}
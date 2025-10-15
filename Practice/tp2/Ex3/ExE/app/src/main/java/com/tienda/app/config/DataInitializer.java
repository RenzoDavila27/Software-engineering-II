package com.tienda.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tienda.app.business.domain.Usuario;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.logic.service.UsuarioService;
import com.tienda.app.business.persistence.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public DataInitializer(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        crearUsuarioAdminSiNoExiste();
    }

    private void crearUsuarioAdminSiNoExiste() {
        Usuario existente = usuarioRepository.buscarUsuarioPorNombre("admin");
        if (existente != null) {
            if (!Boolean.TRUE.equals(existente.getAdministrador())) {
                existente.setAdministrador(true);
                usuarioRepository.save(existente);
            }
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre("admin");
        admin.setPassword("admin");
        admin.setEliminado(false);
        admin.setAdministrador(true);

        try {
            usuarioService.alta(admin);
        } catch (ErrorServiceException e) {
            throw new IllegalStateException("No se pudo crear el usuario administrador por defecto", e);
        }
    }
}

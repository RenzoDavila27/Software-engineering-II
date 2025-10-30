package com.fioritech.gimnasio;

import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.RolUsuario;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.UsuarioService;
import com.fioritech.gimnasio.business.persistence.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class GimnasioApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GimnasioApplication.class);
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "123456";

    public static void main(String[] args) {
        SpringApplication.run(GimnasioApplication.class, args);
    }

    @Bean
    CommandLineRunner ensureDefaultAdmin(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            usuarioRepository.findByNombreUsuarioIgnoreCase(DEFAULT_ADMIN_USERNAME)
                .ifPresentOrElse(admin -> {
                    boolean passwordMatches = false;
                    try {
                        passwordMatches = admin.getClave() != null && encoder.matches(DEFAULT_ADMIN_PASSWORD, admin.getClave());
                    } catch (IllegalArgumentException e) {
                        LOG.warn("No se pudo validar la contraseña del usuario admin, se restablecerá.", e);
                    }

                    if (admin.isEliminado() || admin.getRol() != RolUsuario.ADMINISTRADOR || !passwordMatches) {
                        admin.setEliminado(false);
                        admin.setRol(RolUsuario.ADMINISTRADOR);
                        admin.setClave(encoder.encode(DEFAULT_ADMIN_PASSWORD));
                        usuarioRepository.save(admin);
                        LOG.info("Usuario admin existente actualizado y asegurado con rol ADMINISTRADOR.");
                    }
                }, () -> {
                    try {
                        usuarioService.crearUsuario(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD, RolUsuario.ADMINISTRADOR);
                        LOG.info("Usuario admin creado automáticamente.");
                    } catch (BusinessException e) {
                        LOG.error("No se pudo crear el usuario admin por una inconsistencia de negocio: {}", e.getMessage());
                    } catch (Exception e) {
                        LOG.error("Error inesperado al crear el usuario admin.", e);
                    }
                });
        };
    }
}

package com.fioritech.gimnasio.config;

import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.RolUsuario;
import com.fioritech.gimnasio.business.logic.service.UsuarioService;
import com.fioritech.gimnasio.business.persistence.repository.UsuarioRepository;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Garantiza que exista un usuario administrador base al iniciar la aplicación.
 */
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        final String adminUsername = "administrador";
        final String normalized = adminUsername.toLowerCase(Locale.ROOT);

        Optional<Usuario> existingAdmin = usuarioRepository.findAll().stream()
            .filter(u -> u.getNombreUsuario() != null)
            .filter(u -> u.getNombreUsuario().trim().toLowerCase(Locale.ROOT).equals(normalized))
            .findFirst();

        if (existingAdmin.isPresent()) {
            var usuario = existingAdmin.get();
            boolean updated = false;
            if (usuario.isEliminado()) {
                usuario.setEliminado(false);
                updated = true;
            }
            if (usuario.getRol() != RolUsuario.ADMINISTRADOR) {
                usuario.setRol(RolUsuario.ADMINISTRADOR);
                updated = true;
            }
            if (!esPasswordEncriptada(usuario.getClave())) {
                usuario.setClave(passwordEncoder.encode("123456"));
                updated = true;
            }
            if (updated) {
                usuarioRepository.save(usuario);
            }
            return;
        }

        usuarioService.crearUsuario(adminUsername, "123456", RolUsuario.ADMINISTRADOR);
    }

    private boolean esPasswordEncriptada(String clave) {
        return clave != null && clave.startsWith("$2");
    }
}

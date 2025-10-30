package com.fioritech.gimnasio.config;

import com.fioritech.gimnasio.business.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Bean 1: UserDetailsService
     * Le dice a Spring Security CÓMO buscar un usuario.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Usamos tu repositorio para buscar por 'nombreUsuario', 
        // ya que eso definimos en la entidad Usuario.
        return username -> usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    /**
     * Bean 2: PasswordEncoder
     * Le dice a Spring Security qué encriptación usar.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean 3: AuthenticationManager
     * El gestor que usaremos en nuestro controlador de Login para
     * procesar el intento de autenticación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}


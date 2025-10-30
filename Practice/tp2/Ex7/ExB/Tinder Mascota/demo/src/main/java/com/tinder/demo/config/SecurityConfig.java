package com.tinder.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.tinder.demo.bussines.component.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
                // 1. Desactivar CSRF (no es necesario para APIs stateless)
                .csrf(csrf -> csrf.disable()) 
                .formLogin(formLogin -> formLogin.disable()) // <--- ¡CAMBIO CLAVE!
                .httpBasic(httpBasic -> httpBasic.disable()) // <--- ¡CAMBIO CLAVE!
                .logout(logout -> logout.disable())
                // 2. Definir reglas de autorización
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        // --- Páginas Públicas ---
                        "/",
                        "/index_1", 
                        
                        // --- Flujo de Registro ---
                        "/registro",
                        "/usuario/guardar", 

                        // --- Flujo de Login ---
                        "/login",
                        "/usuario/loginUsuario",

                        // --- Logout ---
                        "/logout",

                        // --- Recursos Estáticos ---
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/vendor/**",
                        "gulpfile.js"

                    ).permitAll()

                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
                )       
                
                // 5. ¡CONFIGURACIÓN STATELESS! No crear ni usar sesiones
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
                )
                
                // 6. Añadir nuestro proveedor de autenticación
                .authenticationProvider(authenticationProvider) 
                
                // 7. ¡AÑADIR NUESTRO FILTRO!
                // Ejecutar `jwtAuthFilter` ANTES del filtro de Username/Password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); 

        return http.build();
    }
}
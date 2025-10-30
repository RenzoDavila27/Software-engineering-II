package com.fioritech.gimnasio.config;

import com.fioritech.gimnasio.business.domain.enums.RolUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
            .csrf(csrf -> csrf.disable()) // Desactivar CSRF

            // 1. Desactivar todos los filtros "stateful" que no usaremos
            .formLogin(formLogin -> formLogin.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .logout(logout -> logout.disable()) // Desactivamos el /logout de Spring

            // 2. Definir las reglas de autorización
            .authorizeHttpRequests(auth -> auth
                
                // 2a. Rutas Públicas (las mismas que en shouldNotFilter)
                .requestMatchers(
                    "/", "/view/login","/login", "/error",
                     "/bootstrap/**", "/images/**", "/tinymce/**"
                ).permitAll()

                // 2b. Reglas para SOCIO
                // (Puede acceder SOCIO, EMPLEADO, y ADMINISTRADOR)
                .requestMatchers(
                    "/cuotaMensual/listaCuotaMensual", "/factura/listaFactura", 
                    "/rutina/listaRutina", "/usuario/logout", "/inicio"
                    // TODO: Añadir las URLs exactas para socios
                ).hasAnyAuthority(
                    RolUsuario.SOCIO.name(),
                    RolUsuario.EMPLEADO.name(),
                    RolUsuario.ADMINISTRADOR.name()
                )

                // 2c. Reglas para EMPLEADO
                // (Puede acceder EMPLEADO y ADMINISTRADOR)
                .requestMatchers(
                    "/promocion/listaPromocion", "/pais/listaPais",
                    "/empleado/listaEmpleado"
                    // TODO: Añadir las URLs exactas para empleados
                ).hasAnyAuthority(
                    RolUsuario.EMPLEADO.name(),
                    RolUsuario.ADMINISTRADOR.name()
                )

                // 2d. Reglas para ADMINISTRADOR
                .requestMatchers(
                    "/sucursal/listaSucursal", "/empresa/listaEmpresa",
                    "/valorCuota/listaValorCuota", "/formaDePago/listaFormaDePago"
                    // TODO: Añadir las URLs exactas para admin
                ).hasAuthority(RolUsuario.ADMINISTRADOR.name())

                // 2e. Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
            )
            
            // 3. Configurar la gestión de sesión como STATELESS
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 4. Añadir nuestro filtro JWT antes del filtro de login estándar
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

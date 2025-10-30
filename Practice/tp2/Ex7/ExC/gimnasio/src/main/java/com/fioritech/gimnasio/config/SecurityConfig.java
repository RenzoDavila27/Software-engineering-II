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
                    "/", "/view/login","/login", "/logout", "/error",
                     "/bootstrap/**", "/images/**", "/tinymce/**",
                    "/mercadopago/webhook"
                ).permitAll()

                // 2b. Reglas para SOCIO
                // (Puede acceder SOCIO, EMPLEADO, y ADMINISTRADOR)
                .requestMatchers(
                    "/view/inicio", "/inicio",
                    "/cuotaMensual/listaCuotaMensual", "/cuotaMensual/consultar/**",
                    "/factura/listaFactura", "/factura/consultar/**",
                    "/rutina/listaRutina", "/rutina/consultar/**",
                    "/mercadopago/success", "/mercadopago/failure", "/mercadopago/pending",
                    "/api/mercadopago/preferences"
                ).hasAnyAuthority(
                    RolUsuario.SOCIO.name(),
                    RolUsuario.EMPLEADO.name(),
                    RolUsuario.ADMINISTRADOR.name()
                )

                // 2c. Reglas para EMPLEADO
                // (Puede acceder EMPLEADO y ADMINISTRADOR)
                .requestMatchers(
                    "/cuotaMensual/altaCuotaMensual", "/cuotaMensual/modificar/**",
                    "/cuotaMensual/baja/**", "/cuotaMensual/aceptarEditCuotaMensual",
                    "/cuotaMensual/cancelarEditCuotaMensual", "/cuotaMensual/buscarCuotasDeSocio",
                    "/cuotaMensual/volver",
                    "/factura/altaFactura", "/factura/modificar/**", "/factura/baja/**",
                    "/factura/aceptarEditFactura", "/factura/cancelarEditFactura",
                    "/rutina/altaRutina", "/rutina/modificar/**", "/rutina/baja/**",
                    "/rutina/aceptarEditRutina", "/rutina/cancelarEditRutina",
                    "/socio/listaSocio", "/socio/altaSocio", "/socio/consultar/**",
                    "/socio/modificar/**", "/socio/baja/**", "/socio/aceptarEditSocio",
                    "/socio/cancelarEditSocio",
                    "/empleado/listaEmpleado", "/empleado/altaEmpleado", "/empleado/consultar/**",
                    "/empleado/modificar/**", "/empleado/baja/**", "/empleado/aceptarEditEmpleado",
                    "/empleado/cancelarEditEmpleado",
                    "/promocion/listaPromocion", "/promocion/altaPromocion", "/promocion/consultar/**",
                    "/promocion/modificar/**", "/promocion/baja/**", "/promocion/aceptarEditPromocion",
                    "/promocion/cancelarEditPromocion", "/promocion/enviarMensaje/**",
                    "/mensaje/listaMensaje", "/mensaje/altaMensaje", "/mensaje/consultar/**",
                    "/mensaje/modificar/**", "/mensaje/baja/**", "/mensaje/aceptarEditMensaje",
                    "/mensaje/cancelarEditMensaje", "/mensaje/enviarMensaje/**",
                    "/pais/listaPais", "/pais/altaPais", "/pais/consultar/**",
                    "/pais/modificar/**", "/pais/baja/**", "/pais/aceptarEditPais", "/pais/cancelarEditPais",
                    "/provincia/listaProvincia", "/provincia/altaProvincia", "/provincia/consultar/**",
                    "/provincia/modificar/**", "/provincia/baja/**", "/provincia/aceptarEditProvincia",
                    "/provincia/cancelarEditProvincia",
                    "/departamento/listaDepartamento", "/departamento/altaDepartamento",
                    "/departamento/consultar/**", "/departamento/modificar/**", "/departamento/baja/**",
                    "/departamento/aceptarEditDepartamento", "/departamento/cancelarEditDepartamento",
                    "/localidad/listaLocalidad", "/localidad/altaLocalidad", "/localidad/consultar/**",
                    "/localidad/modificar/**", "/localidad/baja/**", "/localidad/aceptarEditLocalidad",
                    "/localidad/cancelarEditLocalidad"
                ).hasAnyAuthority(
                    RolUsuario.EMPLEADO.name(),
                    RolUsuario.ADMINISTRADOR.name()
                )

                // 2d. Reglas para ADMINISTRADOR
                .requestMatchers(
                    "/sucursal/listaSucursal", "/sucursal/altaSucursal", "/sucursal/consultar/**",
                    "/sucursal/modificar/**", "/sucursal/baja/**", "/sucursal/aceptarEditSucursal",
                    "/sucursal/cancelarEditSucursal",
                    "/empresa/listaEmpresa", "/empresa/altaEmpresa", "/empresa/consultar/**",
                    "/empresa/modificar/**", "/empresa/baja/**", "/empresa/aceptarEditEmpresa",
                    "/empresa/cancelarEditEmpresa",
                    "/valorCuota/listaValorCuota", "/valorCuota/altaValorCuota",
                    "/valorCuota/consultar/**", "/valorCuota/modificar/**", "/valorCuota/baja/**",
                    "/valorCuota/aceptarEditValorCuota", "/valorCuota/cancelarEditValorCuota",
                    "/formaDePago/listaFormaDePago", "/formaDePago/altaFormaDePago",
                    "/formaDePago/consultar/**", "/formaDePago/modificar/**", "/formaDePago/baja/**",
                    "/formaDePago/aceptarEditFormaDePago", "/formaDePago/cancelarEditFormaDePago"
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

package com.car.clientead.config;

import com.car.clientead.security.RemoteAuthenticationProvider;
import com.car.clientead.security.RoleBasedAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RemoteAuthenticationProvider remoteAuthenticationProvider,
                                                   RoleBasedAuthenticationSuccessHandler successHandler) throws Exception {
        http
            .authenticationProvider(remoteAuthenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/error", "/favicon.ico").permitAll()
                .requestMatchers("/", "/inicio").hasAnyRole("CLIENTE", "ADMINISTRATIVO", "JEFE")
                .requestMatchers("/alquileres/historial", "/alquileres/historial/**")
                    .hasAnyRole("CLIENTE", "ADMINISTRATIVO", "JEFE")
                .requestMatchers("/alquileres/**").hasAnyRole("ADMINISTRATIVO", "JEFE")
                .requestMatchers("/caracteristicas-vehiculo/**", "/vehiculos/**", "/costo-vehiculo/**",
                                 "/documentacion/**", "/promociones/**", "/clientes/**", "/empresas/**",
                                 "/localidades/**", "/departamentos/**", "/provincias/**", "/paises/**",
                                 "/imagenes/**")
                    .hasAnyRole("ADMINISTRATIVO", "JEFE")
                .requestMatchers("/usuarios/**", "/dashboard/**").hasRole("JEFE")
                .anyRequest().hasAnyRole("ADMINISTRATIVO", "JEFE")
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}

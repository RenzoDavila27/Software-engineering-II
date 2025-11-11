package com.fioritech.car.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import java.util.Map;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/error", "/webjars/**", "/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico", "/login", "/register", "/about").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login") // Custom login page if needed
                .defaultSuccessUrl("/exitAccess", true) // Redirect to home after successful login
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/") // Redirect to home after logout
                .permitAll()
            );
        return http.build();
    }

    @Bean
    OAuth2AuthorizationRequestResolver pkceResolver(ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,"/oauth2/authorization");
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }

    @Bean
    DefaultOAuth2UserService userService() {
        DefaultOAuth2UserService service = new DefaultOAuth2UserService();

        service.setAttributesConverter((request) -> (attributes) -> {

            String registrationId = request.getClientRegistration().getRegistrationId();

            if ("twitter".equals(registrationId)) {
                return (Map<String, Object>) attributes.get("data");
            }

            return attributes;
        });

        return service;
    }

}

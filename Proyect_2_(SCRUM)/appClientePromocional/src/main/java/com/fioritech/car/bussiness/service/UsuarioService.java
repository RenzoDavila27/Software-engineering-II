package com.fioritech.car.bussiness.service;

import com.fioritech.car.bussiness.dto.RegistrationForm;
import com.fioritech.car.bussiness.dto.UsuarioApiDto;
import com.fioritech.car.bussiness.mapper.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

// Asumo que tu cliente API se llama UsuarioApiClient
import com.fioritech.car.bussiness.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {

        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public String getEmailFromAuthentication(Authentication authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = oauthToken.getPrincipal();
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        if ("twitter".equals(registrationId) || "x".equals(registrationId)) {
            return principal.getAttribute("confirmed_email");
        } else {
            return principal.getAttribute("email");
        }
    }

    public Mono<String> processUserLogin(String email) {
        return usuarioRepository.loginUserApi(email);
    }

    public Mono<Void> registerUser(RegistrationForm form) {

        // 1. Usar MapStruct para mapear del DTO del formulario al DTO de la API
        UsuarioApiDto apiDto;
        try {
            apiDto = usuarioMapper.registrationFormToApiDto(form);
        } catch (Exception e) {
            System.out.println("Error procesando la foto: " + e.getMessage());
            return Mono.error(new RuntimeException("Error procesando la foto", e));
        }

        // 2. Llama al cliente API con el DTO limpio
        return usuarioRepository.registerUserApi(apiDto);
    }
}
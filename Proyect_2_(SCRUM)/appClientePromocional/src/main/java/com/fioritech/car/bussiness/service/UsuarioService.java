package com.fioritech.car.bussiness.service;

import com.fioritech.car.bussiness.dto.JwtResponse;
import com.fioritech.car.bussiness.dto.OAuthLoginRequest;
import com.fioritech.car.bussiness.dto.RegistrationForm;
import com.fioritech.car.bussiness.dto.UsuarioApiDto;
import com.fioritech.car.bussiness.mapper.UsuarioMapper;
import com.fioritech.car.bussiness.repository.UsuarioRepository;
import com.fioritech.car.bussiness.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
public class UsuarioService {

    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final AuthRepository authRepository;

    @Autowired
    UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, AuthRepository authRepository, UsuarioMapper usuarioMapper) {

        this.usuarioRepository = usuarioRepository;
        this.authRepository = authRepository;
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

    public Mono<JwtResponse> obtainJwtToken(String email) {
        if (!StringUtils.hasText(email)) {
            return Mono.error(() -> new IllegalArgumentException("El email es obligatorio para solicitar el token JWT"));
        }
        OAuthLoginRequest request = new OAuthLoginRequest(email);
        return authRepository.oauthLogin(request);
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

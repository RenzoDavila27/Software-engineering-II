package com.tinder.demo.bussines.logic.service;

import com.tinder.demo.bussines.logic.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import com.tinder.demo.bussines.domain.Usuario;
import com.tinder.demo.bussines.domain.dto.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.tinder.demo.bussines.domain.dto.RegisterRequest;
import com.tinder.demo.bussines.domain.dto.LoginRequest;
import com.tinder.demo.bussines.persistence.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // 1. Método de REGISTRO
    public void register(RegisterRequest request, HttpServletResponse response) {
        // (Aquí podrías añadir validación si el email ya existe)

        var user = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .mail(request.getMail())
                .clave(passwordEncoder.encode(request.getClave())) // ¡Codificar!
                .build();
        
        repository.save(user); // Guardar en la BD
        
        var jwtToken = jwtService.generateToken(user); // Generar token
        
        Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
        jwtCookie.setHttpOnly(true); // ¡Importante! No accesible por JS
        jwtCookie.setSecure(true);   // Solo por HTTPS
        jwtCookie.setPath("/");      // Disponible en todo el sitio
        jwtCookie.setMaxAge(60 * 60 * 24); // 1 día (en segundos)
        
        response.addCookie(jwtCookie); // Añadir la cookie a la respuesta
    }

    // 2. Método de LOGIN
    public void login(LoginRequest request, HttpServletResponse response) {
        // 1. Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getMail(),
                        request.getClave()
                )
        );

        // 2. Si es exitoso, buscar al usuario y generar token
        var user = repository.buscarUsuarioPorMail(request.getMail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        // 3. ¡CREAR Y AÑADIR LA COOKIE!
        Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
        jwtCookie.setHttpOnly(true); // ¡Importante! No accesible por JS
        jwtCookie.setSecure(true);   // Solo por HTTPS
        jwtCookie.setPath("/");      // Disponible en todo el sitio
        jwtCookie.setMaxAge(60 * 60 * 24); // 1 día (en segundos)
        
        response.addCookie(jwtCookie); // Añadir la cookie a la respuesta
    }
}
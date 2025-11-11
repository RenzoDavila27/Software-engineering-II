package com.fioritech.car.components;

import com.fioritech.car.bussiness.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Component
public class RegistrationFilter implements WebFilter {

    private final UsuarioService usuarioService;

    private final List<String> allowedPaths = List.of(
            "/login", "/register", "/exitAccess",
            "/css", "/js", "/img", "/lib", "/api"
    );

    @Autowired
    public RegistrationFilter(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1. Si la ruta es pública dejar pasar
        boolean isAllowed = allowedPaths.stream().anyMatch(path::startsWith);
        if (isAllowed) {
            return chain.filter(exchange);
        }

        // 2. Obtener la autenticación del contexto de seguridad
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    // 3. Si no hay autenticación, dejar pasar (Spring Security lo bloqueará)
                    if (authentication == null || !authentication.isAuthenticated()) {
                        return chain.filter(exchange);
                    }

                    // 4. Obtener el email y comprobar si el usuario existe en el backend
                    String email = usuarioService.getEmailFromAuthentication(authentication);
                    return usuarioService.processUserLogin(email)
                            .then(
                                    // 5. ÉXITO: El usuario existe. Dejarlo pasar al controlador.
                                    chain.filter(exchange)
                            )
                            .onErrorResume(e -> {
                                // 6. ERROR: El usuario no existe. Redirigir a /register
                                return redirectToRegister(exchange);
                            });
                })
                .switchIfEmpty(chain.filter(exchange)); // Si no hay contexto, dejar pasar
    }

    private Mono<Void> redirectToRegister(ServerWebExchange exchange) {
        // Creamos la redirección a la página de registro
        exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER); // 303 Redirect
        exchange.getResponse().getHeaders().setLocation(URI.create("/register"));
        return exchange.getResponse().setComplete();
    }
}
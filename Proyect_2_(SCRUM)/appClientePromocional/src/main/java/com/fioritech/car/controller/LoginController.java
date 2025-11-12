package com.fioritech.car.controller;

import com.fioritech.car.bussiness.dto.RegistrationForm;
import com.fioritech.car.bussiness.service.UsuarioService;
import com.fioritech.car.components.RegistrationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RegistrationFilter registrationFilter;

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
        return "login";
    }

    @GetMapping("/exitAccess")
    public Mono<String> exit(Model model, Authentication authentication) {

        if (authentication == null) {
            return Mono.just("redirect:/login");
        }

        RegistrationForm registrationForm = new RegistrationForm();
        String email = usuarioService.getEmailFromAuthentication(authentication);
        registrationForm.setEmail(email);

        return usuarioService.processUserLogin(email)
                .map(user -> "redirect:/") // En éxito, redirige a index
                .onErrorResume(e -> {
                    model.addAttribute("registrationForm", registrationForm);
                    return Mono.just("register");
                });
    }

}

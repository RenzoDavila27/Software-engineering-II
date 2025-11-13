package com.fioritech.car.controller;

import com.fioritech.car.bussiness.dto.RegistrationForm;
import com.fioritech.car.bussiness.service.UsuarioService;
import com.fioritech.car.components.JwtSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtSessionManager jwtSessionManager;

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
        return "login";
    }

    @GetMapping("/exitAccess")
    public Mono<String> exit(Model model, Authentication authentication, HttpServletRequest request) {

        if (authentication == null) {
            return Mono.just("redirect:/login");
        }

        RegistrationForm registrationForm = new RegistrationForm();
        String email = usuarioService.getEmailFromAuthentication(authentication);
        registrationForm.setEmail(email);

        return usuarioService.processUserLogin(email)
                .flatMap(user -> usuarioService.obtainJwtToken(email)
                        .doOnNext(jwt -> jwtSessionManager.storeTokens(request, jwt))
                        .thenReturn("redirect:/"))
                .onErrorResume(e -> {
                    model.addAttribute("registrationForm", registrationForm);
                    return Mono.just("register");
                });
    }

}

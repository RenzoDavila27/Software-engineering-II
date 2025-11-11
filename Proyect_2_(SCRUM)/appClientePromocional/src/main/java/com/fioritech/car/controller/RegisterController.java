package com.fioritech.car.controller;

import com.fioritech.car.bussiness.dto.RegistrationForm;
import com.fioritech.car.bussiness.repository.UsuarioRepository;
import com.fioritech.car.bussiness.service.RegistrationService;
import com.fioritech.car.bussiness.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class RegisterController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/register")
    public Mono<String> showRegisterPage(Model model, HttpServletRequest request) {

        model.addAttribute("requestURI", request.getRequestURI());
        RegistrationForm registrationForm = new RegistrationForm();

        // Obtenemos la autenticación del contexto
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> {
                    if (authentication != null && authentication.isAuthenticated()) {
                        String email = usuarioService.getEmailFromAuthentication(authentication);

                        registrationForm.setEmail(email);
                        model.addAttribute("registrationForm", registrationForm);
                    }
                    return "register";
                })
                .defaultIfEmpty("register");
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registrationForm") RegistrationForm registrationForm, Model model) {
        List<String> errors = registrationService.validate(registrationForm);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("registrationForm", registrationForm);
            return "register";
        }

        usuarioService.registerUser(registrationForm);

        return "redirect:/index";
    }
}

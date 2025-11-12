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

        // Crear el RegistrationForm una vez
        RegistrationForm registrationForm = new RegistrationForm();

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext(authentication -> { // Usar doOnNext para modificar el formulario
                    if (authentication != null && authentication.isAuthenticated()) {
                        String email = usuarioService.getEmailFromAuthentication(authentication);
                        registrationForm.setEmail(email);
                    }
                })
                .thenReturn(registrationForm) // Devolver el formulario modificado
                .defaultIfEmpty(registrationForm) // Si no hay contexto de seguridad, usar el formulario inicial
                .map(form -> {
                    model.addAttribute("registrationForm", form); // Añadir al modelo aquí
                    return "register";
                });
    }

    @PostMapping("/register")
    public Mono<String> register(@ModelAttribute("registrationForm") RegistrationForm registrationForm, Model model) {
        List<String> errors = registrationService.validate(registrationForm);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("registrationForm", registrationForm);
            return Mono.just("register");
        }

        // --- 2. Llamada Asíncrona (aquí está el cambio) ---

        // Llamamos al servicio, que devuelve un Mono<Void>
        return usuarioService.registerUser(registrationForm)
                .then(Mono.just("index")) // <-- (A) Si el Mono termina con ÉXITO, redirige a index
                .onErrorResume(e -> {

                    model.addAttribute("errors", List.of("Error en el registro: " + e.getMessage()));
                    // Devolvemos el formulario para que el usuario no pierda sus datos
                    model.addAttribute("registrationForm", registrationForm);

                    return Mono.just("register");
                });
    }
}

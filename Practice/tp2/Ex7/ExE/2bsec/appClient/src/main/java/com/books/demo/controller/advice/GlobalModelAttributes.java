package com.books.demo.controller.advice;

import com.books.demo.bussiness.logic.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice
public class GlobalModelAttributes {

    private final AuthService authService;

    public GlobalModelAttributes(AuthService authService) {
        this.authService = authService;
    }

    @ModelAttribute
    public void populateAuthAttributes(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = authService.isAuthenticated(session);
        model.addAttribute("isAuthenticated", authenticated);
        model.addAttribute("isAdmin", authenticated && authService.hasRole(session, AuthService.ROLE_ADMIN));
        model.addAttribute("currentUser", authService.getUsername(session));
    }
}


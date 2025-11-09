package com.fioritech.car.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AboutController {

    @GetMapping("/about")
    public String about(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
        return "about";
    }
}

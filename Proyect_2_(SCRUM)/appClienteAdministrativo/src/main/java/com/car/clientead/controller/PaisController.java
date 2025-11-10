package com.car.clientead.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.car.clientead.business.logic.PaisService;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/paises")
public class PaisController {
    
    private static final String REDIRECT_PAISES = "redirect:/paises";

    @Autowired
    private PaisService paisService;

    @GetMapping
    public String paises(Model model) {
        try {
            model.addAttribute("items", paisService.listarPaises());
        } catch (ApiClientException ex) {
            model.addAttribute("items", java.util.Collections.emptyList());
            Object existing = model.asMap().get("errorMessage");
            String message = existing != null
                    ? existing.toString() + " " + ex.getMessage()
                    : ex.getMessage();
            model.addAttribute("errorMessage", message);
        }
        return "lPais.html";
    }



}

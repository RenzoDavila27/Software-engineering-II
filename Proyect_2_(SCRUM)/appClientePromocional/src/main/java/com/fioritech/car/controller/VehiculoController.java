package com.fioritech.car.controller;

import com.fioritech.car.service.VehiculoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @GetMapping("/vehiculos")
    public String findAll(Model model, HttpServletRequest request) {
        model.addAttribute("vehiculos", vehiculoService.findAll().collectList().block());
        model.addAttribute("requestURI", request.getRequestURI());
        return "vehicle";
    }
}

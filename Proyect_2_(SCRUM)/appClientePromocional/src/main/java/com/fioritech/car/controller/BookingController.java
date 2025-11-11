package com.fioritech.car.controller;

import com.fioritech.car.bussiness.service.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final VehiculoService vehiculoService;

    @GetMapping("/book")
    public String book(@RequestParam(name = "vehiculoId") String vehiculoId, Model model, HttpServletRequest request) {
        model.addAttribute("vehiculo", vehiculoService.findById(vehiculoId).block());
        model.addAttribute("requestURI", request.getRequestURI());
        return "book";
    }
}

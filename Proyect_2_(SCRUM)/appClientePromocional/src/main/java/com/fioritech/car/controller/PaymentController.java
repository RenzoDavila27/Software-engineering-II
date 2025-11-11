package com.fioritech.car.controller;

import com.fioritech.car.bussiness.dto.VehiculoDto;
import com.fioritech.car.bussiness.service.VehiculoService;
import com.fioritech.car.bussiness.service.PaymentService; // Corrected semicolon
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final VehiculoService vehiculoService;
    private final PaymentService paymentService; // Corrected semicolon

    @GetMapping("/payment")
    public String showPaymentPage(@RequestParam("vehiculoId") String vehiculoId,
                                  @RequestParam("rentalDays") int rentalDays,
                                  Model model, HttpServletRequest request) {
        Mono<VehiculoDto> vehiculoMono = vehiculoService.findById(vehiculoId);

        VehiculoDto vehiculo = vehiculoMono.block(); // Blocking for simplicity, consider non-blocking in real app

        if (vehiculo == null) {
            // Handle case where vehicle is not found
            return "redirect:/vehiculos"; // Redirect to vehicles page or an error page
        }

        double costPerDay = vehiculo.getCostoVehiculo().getCosto();
        double totalPrice = rentalDays * costPerDay;

        model.addAttribute("vehiculo", vehiculo);
        model.addAttribute("rentalDays", rentalDays);
        model.addAttribute("costPerDay", costPerDay);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("requestURI", request.getRequestURI());

        return "payment";
    }

    @PostMapping("/payment/process")
    public String processPayment(@RequestParam("vehiculoId") String vehiculoId,
                                 @RequestParam("rentalDays") int rentalDays,
                                 @RequestParam("totalPrice") double totalPrice,
                                 @RequestParam("paymentMethod") String paymentMethod,
                                 Model model) {

        switch (paymentMethod) {
            case "efectivo":
                paymentService.processEfectivoPayment(vehiculoId, rentalDays, totalPrice).block();
                break;
            case "transferencia":
                paymentService.processTransferenciaPayment(vehiculoId, rentalDays, totalPrice).block();
                break;
            case "mercadoPago":
                paymentService.processMercadoPagoPayment(vehiculoId, rentalDays, totalPrice).block();
                break;
        }

        return "redirect:/success"; // Redirect to a success page
    }

    @GetMapping("/success")
    public String exito(){
        return "success";
    }
}

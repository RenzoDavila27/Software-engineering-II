package com.fioritech.car.controller;

import com.fioritech.car.bussiness.dto.VehiculoDto;
import com.fioritech.car.bussiness.service.PaymentService; // Corrected semicolon
import com.fioritech.car.bussiness.service.VehiculoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private static final String SESSION_FECHA_DESDE = "payment.fechaDesde";
    private static final String SESSION_FECHA_HASTA = "payment.fechaHasta";

    private final VehiculoService vehiculoService;
    private final PaymentService paymentService; // Corrected semicolon

    @GetMapping("/payment")
    public String showPaymentPage(@RequestParam("vehiculoId") String vehiculoId,
                                  @RequestParam("rentalDays") int rentalDays,
                                  @RequestParam("fechaDesde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                  @RequestParam("fechaHasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                                  Model model,
                                  HttpServletRequest request) {
        Mono<VehiculoDto> vehiculoMono = vehiculoService.findById(vehiculoId);

        VehiculoDto vehiculo = vehiculoMono.block(); // Blocking for simplicity, consider non-blocking in real app

        if (vehiculo == null) {
            // Handle case where vehicle is not found
            return "redirect:/vehiculos"; // Redirect to vehicles page or an error page
        }

        double costPerDay = vehiculo.getCostoVehiculo().getCosto();
        double totalPrice = rentalDays * costPerDay;

        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_FECHA_DESDE, fechaDesde.toString());
        session.setAttribute(SESSION_FECHA_HASTA, fechaHasta.toString());

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
                                 HttpServletRequest request) {

        LocalDate fechaDesde = getPaymentDate(request, SESSION_FECHA_DESDE);
        LocalDate fechaHasta = getPaymentDate(request, SESSION_FECHA_HASTA);
        if (fechaDesde == null || fechaHasta == null) {
            throw new IllegalStateException("No se encontraron las fechas del alquiler. Volvé a realizar la reserva.");
        }

        switch (paymentMethod) {
            case "efectivo":
                paymentService.processEfectivoPayment(vehiculoId, rentalDays, totalPrice, fechaDesde, fechaHasta).block();
                clearPaymentSession(request);
                return "redirect:/success";
            case "transferencia":
                paymentService.processTransferenciaPayment(vehiculoId, rentalDays, totalPrice, fechaDesde, fechaHasta).block();
                clearPaymentSession(request);
                return "redirect:/success";
            case "mercadoPago":
                String initPoint = paymentService.processMercadoPagoPayment(
                        vehiculoId, rentalDays, totalPrice, fechaDesde, fechaHasta, resolveBaseUrl(request))
                    .block();
                if (initPoint == null || initPoint.isBlank()) {
                    throw new IllegalStateException("No se pudo obtener la URL de Mercado Pago");
                }
                clearPaymentSession(request);
                return "redirect:" + initPoint;
            default:
                throw new IllegalArgumentException("Método de pago no soportado: " + paymentMethod);
        }
    }

    @GetMapping("/success")
    public String exito(){
        return "success";
    }

    private LocalDate getPaymentDate(HttpServletRequest request, String attributeName) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(attributeName);
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof String) {
            String stringValue = (String) value;
            if (!stringValue.isBlank()) {
                return LocalDate.parse(stringValue);
            }
        }
        return null;
    }

    private void clearPaymentSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_FECHA_DESDE);
            session.removeAttribute(SESSION_FECHA_HASTA);
        }
    }

    private String resolveBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        boolean defaultPort = (serverPort == 80 && "http".equalsIgnoreCase(scheme))
            || (serverPort == 443 && "https".equalsIgnoreCase(scheme));
        return scheme + "://" + serverName + (defaultPort ? "" : ":" + serverPort);
    }
}

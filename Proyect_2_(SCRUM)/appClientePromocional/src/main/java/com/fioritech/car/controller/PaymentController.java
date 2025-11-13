package com.fioritech.car.controller;

import com.fioritech.car.bussiness.dto.DocumentoAdjuntoDto;
import com.fioritech.car.bussiness.dto.VehiculoDto;
import com.fioritech.car.bussiness.service.PaymentService; // Corrected semicolon
import com.fioritech.car.bussiness.service.VehiculoService;
import com.fioritech.car.components.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private static final String SESSION_FECHA_DESDE = "payment.fechaDesde";
    private static final String SESSION_FECHA_HASTA = "payment.fechaHasta";
    private static final String SESSION_DOC_DNI = "payment.docDni";
    private static final String SESSION_DOC_LICENCIA = "payment.docLicencia";
    private static final String MERCADO_PAGO_RETURN_BASE_URL =
        "https://arrantly-nonperturbing-darlena.ngrok-free.dev";

    private final VehiculoService vehiculoService;
    private final PaymentService paymentService; // Corrected semicolon
    private final JwtSessionManager jwtSessionManager;

    @PostMapping("/payment")
    public String preparePayment(@RequestParam("vehiculoId") String vehiculoId,
                                 @RequestParam("rentalDays") int rentalDays,
                                 @RequestParam("fechaDesde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                 @RequestParam("fechaHasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                                 @RequestParam("docDni") MultipartFile docDni,
                                 @RequestParam("docLicencia") MultipartFile docLicencia,
                                 HttpServletRequest request) {
        storeDocumentoEnSession(request, SESSION_DOC_DNI, docDni, "DNI");
        storeDocumentoEnSession(request, SESSION_DOC_LICENCIA, docLicencia, "Licencia de conducir");
        return "redirect:/payment?vehiculoId=" + vehiculoId
                + "&rentalDays=" + rentalDays
                + "&fechaDesde=" + fechaDesde
                + "&fechaHasta=" + fechaHasta;
    }

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
                String authorizationHeader = jwtSessionManager.getAuthorizationHeader(request)
                        .orElseThrow(() -> new IllegalStateException("No se encontró la sesión de autenticación. Iniciá sesión nuevamente."));
                DocumentoAdjuntoDto docDni = getDocumentoFromSession(request, SESSION_DOC_DNI)
                        .orElseThrow(() -> new IllegalStateException("No se adjuntó el documento DNI. Volvé a cargar la documentación."));
                DocumentoAdjuntoDto docLicencia = getDocumentoFromSession(request, SESSION_DOC_LICENCIA)
                        .orElseThrow(() -> new IllegalStateException("No se adjuntó la licencia de conducir. Volvé a cargar la documentación."));
                String initPoint = paymentService.processMercadoPagoPayment(
                        vehiculoId, rentalDays, totalPrice, fechaDesde, fechaHasta,
                        MERCADO_PAGO_RETURN_BASE_URL, authorizationHeader, docDni, docLicencia)
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
            session.removeAttribute(SESSION_DOC_DNI);
            session.removeAttribute(SESSION_DOC_LICENCIA);
        }
    }

    private void storeDocumentoEnSession(HttpServletRequest request, String attributeName,
                                         MultipartFile file, String descripcion) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de " + descripcion + " es obligatorio.");
        }
        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            DocumentoAdjuntoDto dto = new DocumentoAdjuntoDto(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    base64
            );
            request.getSession(true).setAttribute(attributeName, dto);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo procesar el archivo de " + descripcion, e);
        }
    }

    private Optional<DocumentoAdjuntoDto> getDocumentoFromSession(HttpServletRequest request, String attributeName) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        Object value = session.getAttribute(attributeName);
        if (value instanceof DocumentoAdjuntoDto dto) {
            return Optional.of(dto);
        }
        return Optional.empty();
    }
}

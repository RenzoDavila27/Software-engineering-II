package com.fioritech.gimnasio.controller.view;

import com.fioritech.gimnasio.business.domain.Factura;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.MercadoPagoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MercadoPagoCallbackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MercadoPagoCallbackController.class);

    private final MercadoPagoService mercadoPagoService;

    public MercadoPagoCallbackController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @GetMapping("/mercadopago/success")
    public String success(@RequestParam(value = "payment_id", required = false) String paymentId,
        @RequestParam(value = "preference_id", required = false) String preferenceId,
        @RequestParam(value = "collection_status", required = false) String collectionStatus,
        RedirectAttributes attributes) {
        try {
            var resultado = mercadoPagoService.processPaymentNotification(paymentId, preferenceId);
            if (resultado.isPresent()) {
                Factura factura = resultado.get();
                attributes.addFlashAttribute("msgExito",
                    "Pago registrado correctamente. Factura N° " + factura.getNumeroFactura());
            } else {
                String estado = collectionStatus != null ? collectionStatus : "pendiente";
                attributes.addFlashAttribute("msgError",
                    "El pago aún se encuentra en proceso (estado: " + estado
                        + "). Mercado Pago notificará cuando se apruebe.");
            }
        } catch (BusinessException ex) {
            attributes.addFlashAttribute("msgError", ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al procesar el pago de Mercado Pago", ex);
            attributes.addFlashAttribute("msgError",
                "Ocurrió un error inesperado al registrar el pago. Intente nuevamente o contacte al administrador.");
        }
        return "redirect:/cuotaMensual/listaCuotaMensual";
    }

    @GetMapping("/mercadopago/failure")
    public String failure(@RequestParam(value = "status", required = false) String status,
        RedirectAttributes attributes) {
        String mensaje = "El pago fue cancelado";
        if (status != null && !status.isBlank()) {
            mensaje += ": " + status;
        }
        attributes.addFlashAttribute("msgError", mensaje);
        return "redirect:/cuotaMensual/listaCuotaMensual";
    }

    @GetMapping("/mercadopago/pending")
    public String pending(@RequestParam(value = "status", required = false) String status,
        RedirectAttributes attributes) {
        String mensaje = "El pago quedó pendiente de confirmación";
        if (status != null && !status.isBlank()) {
            mensaje += ": " + status;
        }
        attributes.addFlashAttribute("msgError", mensaje);
        return "redirect:/cuotaMensual/listaCuotaMensual";
    }
}

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
    public String success(@RequestParam("payment_id") String paymentId,
        @RequestParam(value = "external_reference", required = false) String externalReference,
        RedirectAttributes attributes) {
        try {
            Factura factura = mercadoPagoService.processSuccessfulPayment(paymentId, externalReference);
            attributes.addFlashAttribute("msgExito",
                "Pago registrado correctamente. Factura N° " + factura.getNumeroFactura());
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

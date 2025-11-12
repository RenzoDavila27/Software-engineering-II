package com.car.controller.rest.api;

import com.car.business.logic.error.BusinessException;
import com.car.business.logic.service.MercadoPagoService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mercadopago")
public class MercadoPagoWebhookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MercadoPagoWebhookController.class);

    private final MercadoPagoService mercadoPagoService;

    public MercadoPagoWebhookController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveNotification(
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "topic", required = false) String topic,
        @RequestParam(value = "id", required = false) String paymentIdParam,
        @RequestParam(value = "preference_id", required = false) String preferenceIdParam,
        @RequestBody(required = false) Map<String, Object> body) {

        String eventType = StringUtils.hasText(type) ? type : topic;
        String paymentId = paymentIdParam;
        String preferenceId = preferenceIdParam;

        if (body != null) {
            if (!StringUtils.hasText(eventType) && body.get("type") instanceof String bodyType) {
                eventType = bodyType;
            }
            if (!StringUtils.hasText(paymentId)) {
                paymentId = extractPaymentId(body);
            }
            if (!StringUtils.hasText(preferenceId)) {
                preferenceId = extractPreferenceId(body);
            }
        }

        if (!StringUtils.hasText(eventType) || !"payment".equalsIgnoreCase(eventType)) {
            LOGGER.debug("Webhook de Mercado Pago ignorado. type={}, topic={}", type, topic);
            return ResponseEntity.ok("ignored");
        }

        if (!StringUtils.hasText(paymentId)) {
            LOGGER.warn("Webhook de Mercado Pago sin payment_id (body: {})", body);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("payment_id ausente");
        }

        try {
            Optional<?> result = mercadoPagoService.processPaymentNotification(paymentId, preferenceId);
            if (result.isPresent()) {
                return ResponseEntity.ok("processed");
            }
            return ResponseEntity.ok("pending");
        } catch (BusinessException ex) {
            LOGGER.warn("Error de negocio al procesar webhook de Mercado Pago", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al procesar webhook de Mercado Pago", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }

    @SuppressWarnings("unchecked")
    private String extractPaymentId(Map<String, Object> body) {
        Object data = body.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            Object id = dataMap.get("id");
            if (id != null) {
                return String.valueOf(id);
            }
        }
        Object resource = body.get("resource");
        if (resource instanceof String resourceStr) {
            return extractQueryParam(resourceStr, "id");
        }
        Object payment = body.get("id");
        return payment != null ? String.valueOf(payment) : null;
    }

    @SuppressWarnings("unchecked")
    private String extractPreferenceId(Map<String, Object> body) {
        Object resource = body.get("resource");
        if (resource instanceof String resourceStr) {
            String value = extractQueryParam(resourceStr, "preference_id");
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        Object data = body.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            Object pref = dataMap.get("preference_id");
            if (pref != null) {
                return String.valueOf(pref);
            }
        }
        return null;
    }

    private String extractQueryParam(String url, String param) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            if (!StringUtils.hasText(query)) {
                return null;
            }
            for (String pair : query.split("&")) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2 && param.equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        } catch (URISyntaxException ex) {
            LOGGER.debug("No se pudo analizar la URI del webhook de Mercado Pago: {}", url, ex);
        }
        return null;
    }
}

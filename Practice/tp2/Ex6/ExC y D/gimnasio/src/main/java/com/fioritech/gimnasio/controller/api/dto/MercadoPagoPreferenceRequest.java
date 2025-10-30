package com.fioritech.gimnasio.controller.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record MercadoPagoPreferenceRequest(
    @NotBlank String title,
    String description,
    @NotNull @Positive Integer quantity,
    @NotNull @DecimalMin(value = "0.01") BigDecimal unitPrice,
    String currencyId,
    String externalReference,
    String successUrl,
    String failureUrl,
    String pendingUrl,
    String autoReturn,
    String notificationUrl) {
}

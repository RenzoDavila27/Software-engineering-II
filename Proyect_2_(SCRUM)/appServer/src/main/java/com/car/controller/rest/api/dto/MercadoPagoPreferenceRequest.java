package com.car.controller.rest.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MercadoPagoPreferenceRequest(
    @NotBlank String title,
    String description,
    String currencyId,
    String successUrl,
    String failureUrl,
    String pendingUrl,
    String autoReturn,
    String notificationUrl,
    @NotBlank String vehiculoId,
    @NotNull LocalDate fechaDesde,
    @NotNull LocalDate fechaHasta,
    @Valid @NotNull DocumentoAdjuntoDto docDni,
    @Valid @NotNull DocumentoAdjuntoDto docLicencia) {
}

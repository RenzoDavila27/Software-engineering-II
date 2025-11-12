package com.car.controller.rest.api.dto;

public record MercadoPagoPreferenceResponse(
    String preferenceId,
    String initPoint,
    String sandboxInitPoint) {
}

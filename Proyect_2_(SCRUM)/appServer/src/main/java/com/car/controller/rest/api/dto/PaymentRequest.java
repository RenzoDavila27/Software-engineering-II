package com.car.controller.rest.api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentRequest {

    private String vehiculoId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private double totalPrice;
    private DocumentoAdjuntoDto docDni;
    private DocumentoAdjuntoDto docLicencia;

    public PaymentRequest(String vehiculoId, LocalDate fechaDesde, LocalDate fechaHasta, double totalPrice, DocumentoAdjuntoDto docDni, DocumentoAdjuntoDto docLicencia) {

        this.vehiculoId = vehiculoId;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.totalPrice = totalPrice;
        this.docDni = docDni;
        this.docLicencia = docLicencia;

    }
}

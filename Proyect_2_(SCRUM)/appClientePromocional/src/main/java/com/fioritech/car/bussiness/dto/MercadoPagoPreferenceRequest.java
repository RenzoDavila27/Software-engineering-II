package com.fioritech.car.bussiness.dto;

import java.time.LocalDate;
public class MercadoPagoPreferenceRequest {

    private String title;
    private String description;
    private String currencyId;
    private String successUrl;
    private String failureUrl;
    private String pendingUrl;
    private String autoReturn;
    private String notificationUrl;
    private String vehiculoId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private DocumentoAdjuntoDto docDni;
    private DocumentoAdjuntoDto docLicencia;

    public MercadoPagoPreferenceRequest() {
    }

    public MercadoPagoPreferenceRequest(String title, String description, String currencyId,
                                        String successUrl, String failureUrl, String pendingUrl,
                                        String autoReturn, String notificationUrl, String vehiculoId,
                                        LocalDate fechaDesde, LocalDate fechaHasta,
                                        DocumentoAdjuntoDto docDni, DocumentoAdjuntoDto docLicencia) {
        this.title = title;
        this.description = description;
        this.currencyId = currencyId;
        this.successUrl = successUrl;
        this.failureUrl = failureUrl;
        this.pendingUrl = pendingUrl;
        this.autoReturn = autoReturn;
        this.notificationUrl = notificationUrl;
        this.vehiculoId = vehiculoId;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.docDni = docDni;
        this.docLicencia = docLicencia;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getFailureUrl() {
        return failureUrl;
    }

    public void setFailureUrl(String failureUrl) {
        this.failureUrl = failureUrl;
    }

    public String getPendingUrl() {
        return pendingUrl;
    }

    public void setPendingUrl(String pendingUrl) {
        this.pendingUrl = pendingUrl;
    }

    public String getAutoReturn() {
        return autoReturn;
    }

    public void setAutoReturn(String autoReturn) {
        this.autoReturn = autoReturn;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    public String getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(String vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public DocumentoAdjuntoDto getDocDni() {
        return docDni;
    }

    public void setDocDni(DocumentoAdjuntoDto docDni) {
        this.docDni = docDni;
    }

    public DocumentoAdjuntoDto getDocLicencia() {
        return docLicencia;
    }

    public void setDocLicencia(DocumentoAdjuntoDto docLicencia) {
        this.docLicencia = docLicencia;
    }
}

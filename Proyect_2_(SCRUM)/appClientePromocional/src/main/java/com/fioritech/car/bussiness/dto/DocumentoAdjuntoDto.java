package com.fioritech.car.bussiness.dto;

public class DocumentoAdjuntoDto {

    private String nombreArchivo;
    private String contentType;
    private String contenidoBase64;

    public DocumentoAdjuntoDto() {
    }

    public DocumentoAdjuntoDto(String nombreArchivo, String contentType, String contenidoBase64) {
        this.nombreArchivo = nombreArchivo;
        this.contentType = contentType;
        this.contenidoBase64 = contenidoBase64;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContenidoBase64() {
        return contenidoBase64;
    }

    public void setContenidoBase64(String contenidoBase64) {
        this.contenidoBase64 = contenidoBase64;
    }
}

package com.books.demo.controller.rest.dto;

import com.books.demo.bussiness.domain.ArchivoLibro;
import java.time.LocalDateTime;

public class ArchivoLibroDto {

    private Long libroId;
    private String nombreArchivo;
    private String tipoContenido;
    private Long tamano;
    private LocalDateTime fechaSubida;
    private String rutaArchivo;

    public static ArchivoLibroDto fromEntity(ArchivoLibro archivoLibro) {
        if (archivoLibro == null) {
            return null;
        }
        ArchivoLibroDto dto = new ArchivoLibroDto();
        dto.setLibroId(archivoLibro.getId());
        dto.setNombreArchivo(archivoLibro.getNombreArchivo());
        dto.setTipoContenido(archivoLibro.getTipoContenido());
        dto.setTamano(archivoLibro.getTamano());
        dto.setFechaSubida(archivoLibro.getFechaSubida());
        dto.setRutaArchivo(archivoLibro.getRutaArchivo());
        return dto;
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public Long getTamano() {
        return tamano;
    }

    public void setTamano(Long tamano) {
        this.tamano = tamano;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
}

package com.example.frontend.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideojuegoDto {

    private Long id;
    private String titulo;
    private String rutaimg;
    private float precio;
    private Short cantidad;
    private String descripcion;
    private Boolean oferta;
    private String fechalanzamiento;
    private CategoriaDto categoria;
    private EstudioDto estudio;

    public VideojuegoDto() {
    }

    public VideojuegoDto(Long id, String titulo, String rutaimg, float precio, Short cantidad, String descripcion,
            Boolean oferta, String fechalanzamiento, CategoriaDto categoria, EstudioDto estudio) {
        this.id = id;
        this.titulo = titulo;
        this.rutaimg = rutaimg;
        this.precio = precio;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.oferta = oferta;
        this.fechalanzamiento = fechalanzamiento;
        this.categoria = categoria;
        this.estudio = estudio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getRutaimg() {
        return rutaimg;
    }

    public void setRutaimg(String rutaimg) {
        this.rutaimg = rutaimg;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public Short getCantidad() {
        return cantidad;
    }

    public void setCantidad(Short cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getOferta() {
        return oferta;
    }

    public void setOferta(Boolean oferta) {
        this.oferta = oferta;
    }

    public String getFechalanzamiento() {
        return fechalanzamiento;
    }

    public void setFechalanzamiento(String fechalanzamiento) {
        this.fechalanzamiento = fechalanzamiento;
    }

    public CategoriaDto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDto categoria) {
        this.categoria = categoria;
    }

    public EstudioDto getEstudio() {
        return estudio;
    }

    public void setEstudio(EstudioDto estudio) {
        this.estudio = estudio;
    }
}

package com.fioritech.demo.bussines.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PromocionTipo tipo = PromocionTipo.PROMOCION_GENERAL;

    @Column(nullable = false)
    private boolean eliminado = false;

    public Promocion() {
    }

    public Promocion(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public Promocion(String titulo, String contenido, PromocionTipo tipo) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.tipo = tipo;
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

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public PromocionTipo getTipo() {
        return tipo;
    }

    public void setTipo(PromocionTipo tipo) {
        this.tipo = tipo;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
}

package com.example.demo.business.domain;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="VideoJuego")
public class Videojuego implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="Titulo")
    private String titulo;

    @Column(name = "RutaImg")
    private String rutaimg;

    @Column(name="Precio")
    private float precio;

    @Column(name="Cantidad")
    private Short cantidad;

    @Column(name="Descripcion")
    private String descripcion;

    @Column(name="Oferta")
    private Boolean oferta;

    @Column(name="lanzamiento")
    private String fechalanzamiento;

    @Column(name="Activo")
    private Boolean activo = true;

    //RELACIONES//
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="fk_estudio")
    private Estudio estudio;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="fk_categoria")
    private Categoria categoria;


    //CONSTRUCTORES//

    public Videojuego(){

    }
    public Videojuego(String titulo, String rutaimg, float precio, Short cantidad, String descripcion, Boolean oferta,
            String fechalanzamiento, Boolean activo) {
        this.titulo = titulo;
        this.rutaimg = rutaimg;
        this.precio = precio;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.oferta = oferta;
        this.fechalanzamiento = fechalanzamiento;
        this.activo = activo;
    }

    //GETTERS Y SETTERS//

    public Long getId() {
        return this.id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public String getRutaimg() {
        return this.rutaimg;
    }

    public float getPrecio() {
        return this.precio;
    }

    public Short getCantidad() {
        return this.cantidad;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setRutaimg(String rutaimg) {
        this.rutaimg = rutaimg;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public void setCantidad(Short cantidad) {
        this.cantidad = cantidad;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setOferta(Boolean oferta) {
        this.oferta = oferta;
    }

    public void setFechalanzamiento(String fechalanzamiento) {
        this.fechalanzamiento = fechalanzamiento;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Boolean getOferta() {
        return this.oferta;
    }

    public String getFechalanzamiento() {
        return this.fechalanzamiento;
    }

    public Boolean getActivo() {
        return this.activo;
    }






    
}

package org.entitys;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "articulo")
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String denominacion;
    private int cantidad;
    private int precio;

    @ManyToMany
    private Set<Categoria> categorias;

    public Articulo() {
    }

    public Articulo(String denominacion, int cantidad, int precio) {
        this.denominacion = denominacion;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public void addCategoria(Categoria categoria){
        categorias.add(categoria);
    }

    public void removeCategoria(Categoria categoria){
        categorias.remove(categoria);
    }
}

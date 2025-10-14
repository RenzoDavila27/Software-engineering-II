package com.tienda.app.business.domain;

public class Articulo extends BaseEntity<Long> {
    
    private String nombre;
    private Double precio;
    @ManyToOne
    private Proveedor proveedor;
}

package com.fioritech.demo.bussines.domain;

import jakarta.persistence.*;

@Entity
public class Proveedor extends Persona{

    private String cuit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_id")
    private Direccion direccion;

    public Proveedor() {}

    public Proveedor(String cuit) {
        this.cuit = cuit;
    }

    // Getters y Setters
    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

}

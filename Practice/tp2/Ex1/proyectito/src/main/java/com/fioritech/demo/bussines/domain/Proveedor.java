package com.fioritech.demo.bussines.domain;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Proveedor extends Persona{

    private String cuit;

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

}
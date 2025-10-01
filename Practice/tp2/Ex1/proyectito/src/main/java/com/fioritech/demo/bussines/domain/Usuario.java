package com.fioritech.demo.bussines.domain;

import jakarta.persistence.*;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario extends Persona{

    private String cuenta;
    private String clave;

    public Usuario() {}

    public Usuario(Long id, String cuenta, String clave) {
        this.cuenta = cuenta;
        this.clave = clave;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

}
package com.example.mecanic.bussines.domain.entity;

import jakarta.persistence.Entity;


@Entity
public class Cliente extends Persona {
    
    private String documento;
    public Cliente() {
    }
    public String getDocumento() {
        return documento;
    }
    public void setDocumento(String documento) {
        this.documento = documento;
    }
}

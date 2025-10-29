package com.example.mecanic.bussines.domain.entity;

import com.example.mecanic.bussines.domain.entity.Usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Mecanico extends Persona {
    
    private String legajo;
    @OneToOne
    private Usuario usuario;

    public Mecanico() {
    }
    public String getLegajo() {
        return legajo;
    }
    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}

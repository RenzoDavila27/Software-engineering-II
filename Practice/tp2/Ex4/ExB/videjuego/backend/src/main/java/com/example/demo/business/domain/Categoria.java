package com.example.demo.business.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="Categoria")
public class Categoria extends ClaseBase implements Serializable {

    @Column(name="Nombre")
    private String nombre;

    public Categoria() {
        
    }

    public Categoria(String nombre, boolean activo) {
        this.nombre = nombre;
        this.setEliminado(!activo);
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return !Boolean.TRUE.equals(getEliminado());
    }

}

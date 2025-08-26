package com.example.demo.business.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="Estudio")
public class Estudio implements Serializable {

    @Id
    private String id;

    @Column(name="Nombre")
    private String nombre;

    @Column(name="Activo")
    private boolean activo = true;

    public Estudio() {
        
    }

    public Estudio(String nombre, boolean activo) {
        this.nombre = nombre;
        this.activo = activo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

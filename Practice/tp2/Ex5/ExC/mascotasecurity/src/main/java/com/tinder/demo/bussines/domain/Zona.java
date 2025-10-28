package com.tinder.demo.bussines.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Zona")
public class Zona {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Eliminado")
    private Boolean eliminado = false;

    public Zona(String nombre) {
        this.nombre = nombre;
        this.eliminado = false;
    }

    public Zona(){}

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getEliminado(){
        return this.eliminado;
    }

    public void setEliminado(Boolean eliminado){
        this.eliminado = eliminado;
    }
    

    
    
}

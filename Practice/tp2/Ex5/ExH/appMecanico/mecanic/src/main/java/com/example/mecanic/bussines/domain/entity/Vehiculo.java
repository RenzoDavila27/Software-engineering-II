package com.example.mecanic.bussines.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Vehiculo extends BaseEntity<Long> {

   
    private String marca;
    private String modelo;
    private String patente;
    @ManyToOne
    private Cliente cliente;

    public Vehiculo() {
    }
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }
    @Override
    public Boolean getEliminado() {
        return eliminado;
    }
    @Override
    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }   
}

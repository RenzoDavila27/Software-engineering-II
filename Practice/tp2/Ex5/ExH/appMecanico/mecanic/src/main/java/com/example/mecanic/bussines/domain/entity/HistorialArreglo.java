package com.example.mecanic.bussines.domain.entity;

import java.sql.Date;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


@Entity
public class HistorialArreglo extends BaseEntity<Long> {
    
    private Date fechaArreglo;
    private String detalleArreglo;
    @ManyToOne
    private Vehiculo vehiculo;
    @ManyToOne
    private Mecanico mecanico;


    public HistorialArreglo() {
    }

    @Override
    public Long getId() {
        return id;
    }
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    public Date getFechaArreglo() {
        return fechaArreglo;
    }
    public void setFechaArreglo(Date fechaArreglo) {
        this.fechaArreglo = fechaArreglo;
    }
    public String getDetalleArreglo() {
        return detalleArreglo;
    }
    public void setDetalleArreglo(String detalleArreglo) {
        this.detalleArreglo = detalleArreglo;
    }
    @Override
    public Boolean getEliminado() {
        return eliminado;
    }
    @Override
    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
    public Vehiculo getVehiculo() {
        return vehiculo;
    }
    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }
    public Mecanico getMecanico() {
        return mecanico;
    }
    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }

}

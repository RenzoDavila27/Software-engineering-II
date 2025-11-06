package com.books.demo.bussiness.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "localidades")
public class Localidad extends BaseEntity {

    private String denominacion;

    public Localidad() {
    }

    public Localidad(String denominacion) {
        this.denominacion = denominacion;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }
}

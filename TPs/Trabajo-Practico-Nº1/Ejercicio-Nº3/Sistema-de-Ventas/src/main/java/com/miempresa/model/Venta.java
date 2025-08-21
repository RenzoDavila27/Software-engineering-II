package com.miempresa.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table (name = "Venta")
public class Venta implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PrecioTotal")
    private Integer preciototal;

    @Column (name = "Fecha")
    private String fecha;

    //CONSTRUCTORES
    public Venta(){
    }
    
    public Venta(Integer preciototal, String fecha){
        this.preciototal = preciototal;
        this.fecha = fecha;
        
    }

    //GETTERS Y SETTERS
    public Long getId(){
        return this.id;

    }

    public Integer getPrecioTotal(){
        return this.preciototal;

    }

     public String getFecha(){
        return this.fecha;

    }

    public void setprecioTotal(Integer precio){
        this.preciototal = precio;
    }

    public void setFecha(String fecha){
            this.fecha = fecha;
    }
}



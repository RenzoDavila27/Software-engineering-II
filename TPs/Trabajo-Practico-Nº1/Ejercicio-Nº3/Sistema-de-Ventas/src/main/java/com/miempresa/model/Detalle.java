package com.miempresa.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Detalle")
public class Detalle implements Serializable {

    private static final long serialVersionUID = 1L;

    //ATRIBUTOS

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Subtotal")
    private Integer subtotal;

    @Column(name = "Cantidad")
    private Integer cantidad;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fk_cliente")
    private Cliente cliente;

    //CONSTRUCTORES
    public Detalle(){

    }

    public Detalle(Integer subtotal,Integer cantidad){

        this.cantidad = cantidad;
        this.subtotal = subtotal;
        
    }

    public Detalle(Integer subtotal,Integer cantidad,Cliente cliente){

        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.cliente = cliente;
        
    }

    //GETTERS Y SETTERS
    public Long getId(){
        return this.id;

    }

    public Cliente getCliente(){
        return this.cliente;

    }

    public Integer getSubTotal(){
        return this.subtotal;

    }

     public Integer getCantidad(){
        return this.cantidad;

    }

    public void setCliente(Cliente cliente){
        this.cliente = cliente;
    }

    public void setSubtotal(Integer subtotal){
        this.subtotal = subtotal;
    }

    public void setCantidad(Integer cantidad){
            this.cantidad = cantidad;
    }

}

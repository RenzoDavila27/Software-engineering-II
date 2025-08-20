package com.miempresa.model;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;


@Entity
@Table (name = "Cliente")
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DNI")
    private String dni;

    @Column (name = "Nombre")
    private String nombre;

    @Column (name = "Apellido")
    private String apellido;
    //CONSTRUCTORES
    public Cliente(){
    }
    
    public Cliente(String nombre, String apellido, String dni){
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
    }

    //GETTERS Y SETTERS
    public Long getId(){
        return this.id;

    }

     public String getNombre(){
        return this.nombre;

    }

     public String getApellido(){
        return this.apellido;

    }

     public String getDNI(){
        return this.dni;

    }

    public void setDNI(String dni){
        this.dni = dni;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setApellido(String apellido){
            this.apellido = apellido;
    }
}

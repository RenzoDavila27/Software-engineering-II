package com.example.mecanic.bussines.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.example.mecanic.bussines.domain.enumeration.Rol;  

@Entity
public class Usuario extends BaseEntity<Long> {


    private String nombre;
    private String clave;
  
    @Enumerated(EnumType.ORDINAL)
    private Rol rol;

    public Usuario() {
    }
    @Override
    public Long getId() {
        return id;
    }
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getClave() {
        return clave;
    }
    public void setClave(String clave) {
        this.clave = clave;
    }
    @Override
    public Boolean getEliminado() {
        return eliminado;
    }
    @Override
    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
    public Rol getRol(){
        return this.rol;
    }

    public void setRol(Rol rol){
        this.rol = rol;
    }

}

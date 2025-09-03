package com.tinder.demo.bussines.domain;

import com.tinder.demo.bussines.domain.Usuario;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "Mascota")

public class Mascota{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Sexo")
    private String sexo;

    @Column(name = "Fecha de Alta")
    private Date fechadealta;

    @Column(name = "Fecha de Baja")
    private Date fechadebaja = null;

    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;

    public Mascota(String nombre, String sexo, Date fechadealta, Usuario usuario) {
        this.nombre = nombre;
        this.sexo = sexo;
        this.fechadealta = fechadealta;
        this.usuario = usuario;
    }

    public Mascota(){
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Date getFechadealta() {
        return fechadealta;
    }

    public void setFechadealta(Date fechadealta) {
        this.fechadealta = fechadealta;
    }

    public Date getFechadebaja() {
        return fechadebaja;
    }

    public void setFechadebaja(Date fechadebaja) {
        this.fechadebaja = fechadebaja;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    

    



}

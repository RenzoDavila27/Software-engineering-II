package com.tinder.demo.bussines.domain;

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
@Table(name="Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Apellido")
    private String apellido;

    @Column(name = "Mail")
    private String mail;

    @Lob
    @Column(columnDefinition = "LONGBLOB") // FOTO MYSQL
    private byte[] foto;

    @Column(name = "Clave")
    private String clave;

    @Column(name= "Fecha de Alta")
    private Date fechadealta;

    @Column(name = "Fecha de Baja")
    private Date fechadebaja = null;

    @ManyToOne
    @JoinColumn(name = "fk_zona")
    private Zona zona;

    

    public Usuario(String nombre, String apellido, String mail, byte[] foto, String clave, Date fechadealta,Zona zona) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.foto = foto;
        this.clave = clave;
        this.fechadealta = fechadealta;
        this.zona = zona;
    }

    public Usuario(){
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
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

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    

    
}

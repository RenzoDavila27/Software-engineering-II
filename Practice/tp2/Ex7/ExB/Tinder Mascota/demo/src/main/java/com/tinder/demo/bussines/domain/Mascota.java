package com.tinder.demo.bussines.domain;

import com.tinder.demo.bussines.domain.Usuario;
import com.tinder.demo.bussines.domain.Tipo;
import com.tinder.demo.bussines.domain.Sexo;
import java.util.Date;

import jakarta.persistence.*;


@Entity
@Table(name = "Mascota")

public class Mascota{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Nombre")
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Lob
    @Column(columnDefinition = "LONGBLOB") // FOTO MYSQL
    private byte[] foto;

    @Column(name = "Tipo de Foto")
    private String fotoTipo;

    @Column(name = "Fecha de Alta")
    private Date fechadealta;

    @Column(name = "Fecha de Baja")
    private Date fechadebaja = null;

    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;

    public Mascota(String nombre, Sexo sexo, Tipo tipo, byte[] foto, Date fechadealta, Usuario usuario) {
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getFotoTipo() {
        return fotoTipo;
    }

    public void setFotoTipo(String fotoTipo) {
        this.fotoTipo = fotoTipo;
    }
}

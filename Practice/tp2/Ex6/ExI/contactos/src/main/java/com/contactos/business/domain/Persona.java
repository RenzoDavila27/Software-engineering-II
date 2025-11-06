package com.contactos.business.domain;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.contactos.business.domain.BaseEntity;

@Entity
@Table(name = "personas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Persona extends BaseEntity<Long> {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference("persona-empresas")
    private Set<Empresa> empresas = new HashSet<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference("persona-usuarios")
    private Set<Usuario> usuarios = new HashSet<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference("persona-contactos")
    private Set<Contacto> contactos = new HashSet<>();

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Set<Empresa> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(Set<Empresa> empresas) {
        this.empresas = empresas;
    }

    public Set<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Set<Contacto> getContactos() {
        return contactos;
    }

    public void setContactos(Set<Contacto> contactos) {
        this.contactos = contactos;
    }

    @Override
    public Boolean isEliminado() {
        return eliminado;
    }

    @Override
    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
}

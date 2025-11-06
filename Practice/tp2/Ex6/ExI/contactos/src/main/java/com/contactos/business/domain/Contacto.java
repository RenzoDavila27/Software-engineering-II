package com.contactos.business.domain;

import com.contactos.business.domain.enumeration.TipoContacto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "contactos")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class Contacto extends BaseEntity<Long> {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contacto", nullable = false)
    private TipoContacto tipoContacto;

    @Column(length = 1024)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    @JsonBackReference("persona-contactos")
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    @JsonBackReference("empresa-contactos")
    private Empresa empresa;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public TipoContacto getTipoContacto() {
        return tipoContacto;
    }

    public void setTipoContacto(TipoContacto tipoContacto) {
        this.tipoContacto = tipoContacto;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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

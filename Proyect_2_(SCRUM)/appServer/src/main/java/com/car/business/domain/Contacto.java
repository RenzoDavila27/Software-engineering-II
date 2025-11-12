package com.car.business.domain;

import com.car.business.domain.enums.TipoContacto;
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
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@Entity
@ToString
@Table(name = "contactos")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class Contacto extends BaseEntity<String> {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contacto", nullable = false)
    private TipoContacto tipoContacto;

    @Column(length = 1024)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}

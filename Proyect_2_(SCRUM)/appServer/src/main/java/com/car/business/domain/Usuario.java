package com.car.business.domain;

import com.car.business.domain.enums.RolUsuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Usuario extends BaseEntity<String> {

    @Column(nullable = false, unique = true)
    private String nombreUsuario;

    @Column(nullable = false)
    private String clave;

    @Column(nullable = false)
    private RolUsuario rolUsuario;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
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

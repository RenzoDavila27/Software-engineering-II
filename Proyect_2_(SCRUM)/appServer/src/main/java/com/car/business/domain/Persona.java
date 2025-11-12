package com.car.business.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.car.business.domain.enums.TipoDocumento;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@Entity
@Table(name = "personas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Persona extends BaseEntity<String> {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false)
    private String numeroDocumento;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contacto> contactos = new ArrayList<>();

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Direccion direccion;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Imagen imagen;

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}

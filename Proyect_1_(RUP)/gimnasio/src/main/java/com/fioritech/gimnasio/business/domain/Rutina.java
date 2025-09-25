package com.fioritech.gimnasio.business.domain;

import com.fioritech.gimnasio.business.domain.enums.EstadoRutina;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rutinas")
public class Rutina extends BaseEntity {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_finalizacion")
    private LocalDate fechaFinalizacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_rutina", nullable = false)
    private EstadoRutina estadoRutina;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profesor_id", nullable = false)
    private Empleado profesor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "socio_id", nullable = false)
    private Socio socio;

    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRutina> detalles = new ArrayList<>();
}

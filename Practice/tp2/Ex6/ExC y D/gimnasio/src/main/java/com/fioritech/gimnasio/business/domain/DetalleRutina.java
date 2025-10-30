package com.fioritech.gimnasio.business.domain;

import com.fioritech.gimnasio.business.domain.enums.EstadoDetalleRutina;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "detalle_rutina")
public class DetalleRutina extends BaseEntity {

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String actividad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_detalle", nullable = false)
    private EstadoDetalleRutina estadoDetalle;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rutina_id", nullable = false)
    private Rutina rutina;
}

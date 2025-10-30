package com.fioritech.gimnasio.business.domain;

import com.fioritech.gimnasio.business.domain.enums.EstadoCuotaMensual;
import com.fioritech.gimnasio.business.domain.enums.Mes;
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
@Table(name = "cuotas_mensuales")
public class CuotaMensual extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Mes mes;

    @Column(nullable = false)
    private Long anio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoCuotaMensual estado;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "socio_id", nullable = false)
    private Socio socio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "valor_cuota_id", nullable = false)
    private ValorCuota valorCuota;

    @OneToMany(mappedBy = "cuotaMensual")
    private List<DetalleFactura> detallesFactura = new ArrayList<>();
}

package com.fioritech.gimnasio.business.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "valores_cuota")
public class ValorCuota extends BaseEntity {

    @Column(name = "fecha_desde", nullable = false)
    private LocalDate fechaDesde;

    @Column(name = "fecha_hasta")
    private LocalDate fechaHasta;

    @Column(name = "valor_cuota", nullable = false)
    private double valorCuota;

    @OneToMany(mappedBy = "valorCuota")
    private List<CuotaMensual> cuotas = new ArrayList<>();
}

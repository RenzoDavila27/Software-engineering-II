package com.fioritech.gimnasio.business.domain;

import com.fioritech.gimnasio.business.domain.enums.TipoPago;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "formas_pago")
public class FormaDePago extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false)
    private TipoPago tipoPago;

    @Column(length = 512)
    private String observacion;

    @OneToMany(mappedBy = "formaDePago")
    private List<Factura> facturas = new ArrayList<>();
}

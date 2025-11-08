package com.car.business.domain;

import com.car.business.domain.enums.TipoPago;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class FormaDePago extends BaseEntity{

    private TipoPago tipoPago;
    private String observacion;

}

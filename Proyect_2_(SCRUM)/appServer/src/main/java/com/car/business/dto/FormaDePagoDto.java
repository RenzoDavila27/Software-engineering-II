package com.car.business.dto;

import com.car.business.domain.enums.TipoPago;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FormaDePagoDto extends BaseDto<String> {

    private TipoPago tipoPago;
    private String observacion;
}

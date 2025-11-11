package com.car.clientead.client.dto;

import com.car.clientead.client.dto.enums.TipoPago;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FormaDePagoDto extends BaseDto<String> {

    private TipoPago tipoPago;
    private String observacion;
}


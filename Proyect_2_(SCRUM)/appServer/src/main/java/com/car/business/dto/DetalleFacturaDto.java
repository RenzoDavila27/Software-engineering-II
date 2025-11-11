package com.car.business.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DetalleFacturaDto extends BaseDto<String> {

    private int cantidad;
    private double subtotal;
    private String alquilerId;
    private String promocionId;
}

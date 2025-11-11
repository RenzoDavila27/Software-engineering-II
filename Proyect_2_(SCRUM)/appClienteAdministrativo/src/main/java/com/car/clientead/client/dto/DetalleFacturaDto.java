package com.car.clientead.client.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DetalleFacturaDto extends BaseDto<String> {

    private Integer cantidad;
    private Double subtotal;
    private String alquilerId;
    private String promocionId;
}

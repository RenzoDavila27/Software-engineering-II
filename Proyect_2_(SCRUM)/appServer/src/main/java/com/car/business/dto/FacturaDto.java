package com.car.business.dto;

import com.car.business.domain.enums.EstadoFactura;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FacturaDto extends BaseDto<String> {

    private Long numeroFactura;
    private LocalDate fechaFactura;
    private Double totalPagado;
    private EstadoFactura estado;
    private List<String> detalleIds;
}

package com.car.business.dto;

import com.car.business.domain.enums.TipoDocumento;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonaDto extends BaseDto<String> {

    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String contactoId;
    private String direccionId;
    private String imagenId;
}

package com.car.clientead.client.dto;

import java.time.LocalDate;

import com.car.clientead.client.dto.enums.TipoDocumento;

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

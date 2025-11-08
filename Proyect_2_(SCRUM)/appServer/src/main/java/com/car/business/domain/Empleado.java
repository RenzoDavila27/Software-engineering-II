package com.car.business.domain;

import com.car.business.domain.enums.TipoEmpleado;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode
public class Empleado extends Persona {

    private TipoEmpleado tipoEmpleado;

}

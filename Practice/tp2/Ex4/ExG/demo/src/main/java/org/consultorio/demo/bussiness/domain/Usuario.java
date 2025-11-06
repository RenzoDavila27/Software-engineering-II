package org.consultorio.demo.bussiness.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.consultorio.demo.bussiness.domain.enums.Rol;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Usuario extends TemplateEntity{

    private String nombreUsuario;
    private String clave;
    
    @Enumerated(EnumType.STRING)
    private Rol rol;

}

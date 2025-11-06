package org.consultorio.demo.bussiness.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Paciente extends TemplateEntity{

    private String nombre;
    private String apellido;
    private String documento;
    
    @OneToOne
    private Usuario usuario;

}

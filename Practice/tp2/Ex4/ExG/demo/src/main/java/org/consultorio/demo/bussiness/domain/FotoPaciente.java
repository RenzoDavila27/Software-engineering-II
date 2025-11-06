package org.consultorio.demo.bussiness.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class FotoPaciente extends TemplateEntity{

    private String nombre;

    @Lob
    private byte[] contenido;

    @ManyToOne
    private Paciente paciente;

}

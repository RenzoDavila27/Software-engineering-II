package org.consultorio.demo.bussiness.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class HistoriaClinica extends TemplateEntity{

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Paciente paciente;

}

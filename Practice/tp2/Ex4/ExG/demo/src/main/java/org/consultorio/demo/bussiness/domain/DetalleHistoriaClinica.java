package org.consultorio.demo.bussiness.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class DetalleHistoriaClinica extends TemplateEntity{
    private LocalDate fechaHistoria;
    private String detalleHistoria;

    @ManyToOne
    private Medico medico;

    @ManyToOne
    private HistoriaClinica historiaClinica;
}

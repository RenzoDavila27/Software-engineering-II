package org.consultorio.demo.bussiness.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

@Data
@MappedSuperclass
public abstract class TemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;
    
    protected boolean eliminado = false;

}

package com.car.business.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
public abstract class BaseEntity<ID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    protected String id;

    @Column(nullable = false)
    protected boolean eliminado = false;

    public abstract ID getId();
    public abstract void setId(ID id);
    public abstract Boolean getEliminado();
    public abstract void setEliminado(Boolean eliminado);
}

package com.contactos.business.domain;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity<ID> {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
	
	@Column(nullable = false)
    protected Boolean eliminado = false;
	
	public abstract ID getId();
    public abstract void setId(ID id);
    public abstract Boolean isEliminado();
    public abstract void setEliminado(Boolean eliminado);
}

package com.books.demo.bussiness.domain;

/**
 * Entidad base utilizada únicamente para compartir la estructura con los controladores genéricos del cliente.
 * No persiste datos en esta aplicación; actúa como contrato mínimo.
 */
import java.io.Serializable;

public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Boolean eliminado = Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public boolean isEliminado() {
        return Boolean.TRUE.equals(eliminado);
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = Boolean.TRUE.equals(eliminado);
    }

    public void setEliminado(boolean eliminado) {
        setEliminado(Boolean.valueOf(eliminado));
    }
    
}

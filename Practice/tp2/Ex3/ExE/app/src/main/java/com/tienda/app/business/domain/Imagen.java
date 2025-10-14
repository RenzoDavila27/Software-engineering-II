package com.tienda.app.business.domain;
import jakarta.persistence.Entity;
import com.tienda.app.business.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public abstract class Imagen extends BaseEntity<Long> {
    
    private String nombre;
    private String mime;
    private byte[] contenido;
    
}

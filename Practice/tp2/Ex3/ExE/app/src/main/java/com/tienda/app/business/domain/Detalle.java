package com.tienda.app.business.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import com.tienda.app.business.domain.BaseEntity;
import com.tienda.app.business.domain.Articulo;
import com.tienda.app.business.domain.Imagen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Detalle extends BaseEntity<Long> {
    
    
    @ManyToOne
    private Articulo articulo;
    @ManyToOne
    private Imagen imagen;

    
}
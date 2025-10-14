package com.tienda.app.business.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import com.tienda.app.business.domain.BaseEntity;
import com.tienda.app.business.domain.Usuario;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Carrito extends BaseEntity<Long> {

    private Double total;
    @ManyToOne
    private Usuario usuario;
    
}

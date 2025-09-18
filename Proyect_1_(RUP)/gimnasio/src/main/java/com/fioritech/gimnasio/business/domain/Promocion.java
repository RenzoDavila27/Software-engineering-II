package com.fioritech.gimnasio.business.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "promociones")
public class Promocion extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDate fechaEnvioPromocion;

    @Column(name = "cantidad_socios_enviados")
    private Long cantidadSociosEnviados;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 2048)
    private String texto;
}

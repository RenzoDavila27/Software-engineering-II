package com.fioritech.gimnasio.business.domain;

import com.fioritech.gimnasio.business.domain.enums.TipoMensaje;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mensajes")
public class Mensaje extends BaseEntity {

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 2048)
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mensaje", nullable = false)
    private TipoMensaje tipoMensaje;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}

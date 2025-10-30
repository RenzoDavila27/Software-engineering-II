package com.fioritech.gimnasio.business.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "promociones")
public class Promocion extends Mensaje {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(jakarta.persistence.TemporalType.DATE)
	private Date fechaEnvioPromocion; 

    @Column(name = "cantidad_socios_enviados")
    private Long cantidadSociosEnviados;

}

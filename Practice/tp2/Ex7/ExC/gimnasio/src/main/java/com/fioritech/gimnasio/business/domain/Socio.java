package com.fioritech.gimnasio.business.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "socios")
public class Socio extends Persona {

    @Column(name = "numero_socio", nullable = false, unique = true)
    private Long numeroSocio;

    @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL)
    private List<Rutina> rutinas = new ArrayList<>();

    @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL)
    private List<CuotaMensual> cuotasMensuales = new ArrayList<>();

    @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL)
    private List<Factura> facturas = new ArrayList<>();
}

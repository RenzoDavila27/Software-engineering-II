package com.fioritech.gimnasio.business.domain;

import com.fioritech.gimnasio.business.domain.enums.TipoEmpleado;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "empleados")
public class Empleado extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_empleado", nullable = false)
    private TipoEmpleado tipoEmpleado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @OneToOne(optional = true)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "profesor", cascade = CascadeType.ALL)
    private List<Rutina> rutinas = new ArrayList<>();
}

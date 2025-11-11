package com.car.clientead.business.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.VehiculoDto;
import com.car.clientead.client.dto.enums.EstadoVehiculo;
import com.car.clientead.repository.VehiculoRepository;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository repository;

    public List<VehiculoDto> listar() {
        return repository.findAll().stream()
                .filter(this::vehiculoValido)
                .collect(Collectors.toList());
    }

    public VehiculoDto consultar(String id) {
        return repository.findById(id);
    }

    public VehiculoDto crear(VehiculoDto dto) {
        validar(dto);
        return repository.create(dto);
    }

    public VehiculoDto modificar(String id, VehiculoDto dto) {
        validar(dto);
        return repository.update(id, dto);
    }

    public void eliminar(String id) {
        repository.delete(id);
    }

    private void validar(VehiculoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos del vehículo no pueden ser nulos.");
        }
        if (!StringUtils.hasText(dto.getPatente())) {
            throw new IllegalArgumentException("La patente del vehículo es obligatoria.");
        }
        if (!StringUtils.hasText(dto.getCaracteristicaVehiculoId())) {
            throw new IllegalArgumentException("Debe seleccionar una característica de vehículo.");
        }
        if (dto.getEstadoVehiculo() == null) {
            dto.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
        }
    }

    private boolean vehiculoValido(VehiculoDto dto) {
        return dto != null && StringUtils.hasText(dto.getPatente());
    }
}

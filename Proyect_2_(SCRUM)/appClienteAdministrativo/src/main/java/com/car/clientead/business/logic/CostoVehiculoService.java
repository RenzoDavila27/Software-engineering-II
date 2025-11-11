package com.car.clientead.business.logic;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.car.clientead.client.dto.CostoVehiculoDto;
import com.car.clientead.repository.CostoVehiculoRepository;

@Service
public class CostoVehiculoService {

    @Autowired
    private CostoVehiculoRepository repository;

    public List<CostoVehiculoDto> listar() {
        return repository.findAll().stream()
                .filter(this::costoValido)
                .collect(Collectors.toList());
    }

    public CostoVehiculoDto consultar(String id) {
        return repository.findById(id);
    }

    public CostoVehiculoDto crear(CostoVehiculoDto dto) {
        validar(dto);
        return repository.create(dto);
    }

    public CostoVehiculoDto modificar(String id, CostoVehiculoDto dto) {
        validar(dto);
        return repository.update(id, dto);
    }

    public void eliminar(String id) {
        repository.delete(id);
    }

    private void validar(CostoVehiculoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos del costo no pueden ser nulos.");
        }
        if (dto.getFechaDesde() == null) {
            dto.setFechaDesde(LocalDate.now());
        }
        if (dto.getFechaHasta() != null && dto.getFechaHasta().isBefore(dto.getFechaDesde())) {
            throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde.");
        }
        if (dto.getCosto() == null || dto.getCosto() <= 0) {
            throw new IllegalArgumentException("El costo debe ser mayor a cero.");
        }
    }

    private boolean costoValido(CostoVehiculoDto dto) {
        return dto != null && dto.getCosto() != null;
    }
}

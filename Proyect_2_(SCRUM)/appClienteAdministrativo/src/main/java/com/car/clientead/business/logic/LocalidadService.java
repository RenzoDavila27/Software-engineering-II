package com.car.clientead.business.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.LocalidadDto;
import com.car.clientead.repository.LocalidadRepository;

@Service
public class LocalidadService {

    @Autowired
    private LocalidadRepository repository;

    public List<LocalidadDto> listar() {
        return repository.findAll().stream()
                .filter(this::localidadValida)
                .collect(Collectors.toList());
    }

    public LocalidadDto consultar(String id) {
        return repository.findById(id);
    }

    public LocalidadDto crear(LocalidadDto dto) {
        validar(dto);
        return repository.create(dto);
    }

    public LocalidadDto modificar(String id, LocalidadDto dto) {
        validar(dto);
        return repository.update(id, dto);
    }

    public void eliminar(String id) {
        repository.delete(id);
    }

    private boolean localidadValida(LocalidadDto dto) {
        return dto != null && StringUtils.hasText(dto.getNombre());
    }

    private void validar(LocalidadDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos de la localidad no pueden ser nulos.");
        }
        if (!StringUtils.hasText(dto.getNombre())) {
            throw new IllegalArgumentException("El nombre de la localidad es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getCodigoPostal())) {
            throw new IllegalArgumentException("El código postal es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getDepartamentoId())) {
            throw new IllegalArgumentException("Debe seleccionar un departamento.");
        }
    }
}

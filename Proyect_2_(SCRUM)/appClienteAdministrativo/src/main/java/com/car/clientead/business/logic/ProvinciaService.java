package com.car.clientead.business.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.ProvinciaDto;
import com.car.clientead.repository.ProvinciaRepository;

@Service
public class ProvinciaService {

    @Autowired
    private ProvinciaRepository repository;

    public List<ProvinciaDto> listar() {
        return repository.findAll().stream()
                .filter(this::provinciaValida)
                .collect(Collectors.toList());
    }

    public ProvinciaDto consultar(String id) {
        return repository.findById(id);
    }

    public ProvinciaDto crear(ProvinciaDto dto) {
        validar(dto);
        return repository.create(dto);
    }

    public ProvinciaDto modificar(String id, ProvinciaDto dto) {
        validar(dto);
        return repository.update(id, dto);
    }

    public void eliminar(String id) {
        repository.delete(id);
    }

    private boolean provinciaValida(ProvinciaDto dto) {
        return dto != null && StringUtils.hasText(dto.getNombre());
    }

    private void validar(ProvinciaDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos de la provincia no pueden ser nulos.");
        }
        if (!StringUtils.hasText(dto.getNombre())) {
            throw new IllegalArgumentException("El nombre de la provincia es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getPaisId())) {
            throw new IllegalArgumentException("Debe seleccionar un país.");
        }
    }
}

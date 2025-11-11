package com.car.clientead.business.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.DepartamentoDto;
import com.car.clientead.repository.DepartamentoRepository;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository repository;

    public List<DepartamentoDto> listar() {
        return repository.findAll().stream()
                .filter(this::departamentoValido)
                .collect(Collectors.toList());
    }

    public DepartamentoDto consultar(String id) {
        return repository.findById(id);
    }

    public DepartamentoDto crear(DepartamentoDto dto) {
        validar(dto);
        return repository.create(dto);
    }

    public DepartamentoDto modificar(String id, DepartamentoDto dto) {
        validar(dto);
        return repository.update(id, dto);
    }

    public void eliminar(String id) {
        repository.delete(id);
    }

    private boolean departamentoValido(DepartamentoDto dto) {
        return dto != null && StringUtils.hasText(dto.getNombre());
    }

    private void validar(DepartamentoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos del departamento no pueden ser nulos.");
        }
        if (!StringUtils.hasText(dto.getNombre())) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getProvinciaId())) {
            throw new IllegalArgumentException("Debe seleccionar una provincia.");
        }
    }
}

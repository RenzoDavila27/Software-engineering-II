package com.car.clientead.business.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.PaisDto;
import com.car.clientead.repository.PaisRepository;

@Service
public class PaisService {
    
    @Autowired
    private PaisRepository paisRepository;

    public List<PaisDto> listarPaises() {
        return paisRepository.findAll().stream()
                .filter(this::paisValido)
                .collect(Collectors.toList());
    }

    private boolean paisValido(PaisDto pais) {
        return pais != null && StringUtils.hasText(pais.getNombre());
    }

    private void validarPais(PaisDto pais) {
        if (pais == null) {
            throw new IllegalArgumentException("Los datos del pais no pueden ser nulos.");
        }
        if (!StringUtils.hasText(pais.getNombre())) {
            throw new IllegalArgumentException("El nombre del pais es obligatoria.");
        }
    }

}

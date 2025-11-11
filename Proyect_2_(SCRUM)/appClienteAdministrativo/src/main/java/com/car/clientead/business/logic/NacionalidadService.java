package com.car.clientead.business.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.NacionalidadDto;
import com.car.clientead.repository.NacionalidadRepository;

@Service
public class NacionalidadService {

    @Autowired
    private NacionalidadRepository repository;

    public List<NacionalidadDto> listar() {
        return repository.findAll().stream()
                .filter(this::esValida)
                .collect(Collectors.toList());
    }

    private boolean esValida(NacionalidadDto dto) {
        return dto != null && StringUtils.hasText(dto.getNombre());
    }
}

package com.car.clientead.business.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.PersonaDto;
import com.car.clientead.repository.PersonaRepository;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository repository;

    public List<PersonaDto> listar() {
        return repository.findAll().stream()
                .filter(this::personaValida)
                .collect(Collectors.toList());
    }

    public PersonaDto consultar(String id) {
        return repository.findById(id);
    }

    private boolean personaValida(PersonaDto dto) {
        return dto != null && StringUtils.hasText(dto.getNombre());
    }
}

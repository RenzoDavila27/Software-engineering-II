package com.books.demo.bussiness.logic;

import com.books.demo.client.dto.LocalidadDto;
import com.books.demo.repository.LocalidadRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LocalidadService {

    private final LocalidadRepository localidadRepository;

    public LocalidadService(LocalidadRepository localidadRepository) {
        this.localidadRepository = localidadRepository;
    }

    public List<LocalidadDto> listarLocalidades() {
        return localidadRepository.findAll().stream()
                .filter(localidad -> localidad != null && StringUtils.hasText(localidad.getDenominacion()))
                .sorted(Comparator.comparing(LocalidadDto::getDenominacion, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Optional<LocalidadDto> obtenerLocalidad(Long id) {
        return localidadRepository.findById(id);
    }
}

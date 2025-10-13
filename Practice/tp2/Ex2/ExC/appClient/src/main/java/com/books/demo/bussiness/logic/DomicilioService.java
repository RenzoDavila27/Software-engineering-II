package com.books.demo.bussiness.logic;

import com.books.demo.client.dto.DomicilioDto;
import com.books.demo.client.exception.ApiClientException;
import com.books.demo.repository.DomicilioRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DomicilioService {

    private final DomicilioRepository domicilioRepository;

    public DomicilioService(DomicilioRepository domicilioRepository) {
        this.domicilioRepository = domicilioRepository;
    }

    public List<DomicilioDto> listarDomicilios() {
        return domicilioRepository.findAll().stream()
                .filter(this::domicilioValido)
                .collect(Collectors.toList());
    }

    public Optional<DomicilioDto> obtenerDomicilio(Long id) {
        return domicilioRepository.findById(id)
                .map(dto -> domicilioValido(dto) ? dto : null);
    }

    public DomicilioDto crearDomicilio(DomicilioDto domicilio) {
        validarDomicilio(domicilio);
        domicilio.setId(null);
        return domicilioRepository.save(domicilio);
    }

    public DomicilioDto actualizarDomicilio(Long id, DomicilioDto domicilio) {
        if (id == null) {
            throw new IllegalArgumentException("El id del domicilio es obligatorio.");
        }
        validarDomicilio(domicilio);
        domicilio.setId(id);
        return domicilioRepository.update(id, domicilio)
                .orElseThrow(() -> new ApiClientException(
                        "La API no devolvio datos al actualizar el domicilio con id " + id + "."));
    }

    public void eliminarDomicilio(Long id) {
        domicilioRepository.deleteById(id);
    }

    private boolean domicilioValido(DomicilioDto domicilio) {
        return domicilio != null && StringUtils.hasText(domicilio.getCalle());
    }

    private void validarDomicilio(DomicilioDto domicilio) {
        if (domicilio == null) {
            throw new IllegalArgumentException("Los datos del domicilio no pueden ser nulos.");
        }
        if (!StringUtils.hasText(domicilio.getCalle())) {
            throw new IllegalArgumentException("La calle del domicilio es obligatoria.");
        }
        if (domicilio.getNumero() != null && domicilio.getNumero() < 0) {
            throw new IllegalArgumentException("El numero de la calle no puede ser negativo.");
        }
    }
}

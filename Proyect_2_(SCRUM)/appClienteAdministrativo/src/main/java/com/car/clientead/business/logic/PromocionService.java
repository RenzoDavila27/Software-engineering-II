package com.car.clientead.business.logic;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.PromocionDto;
import com.car.clientead.repository.PromocionRepository;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepository repository;

    public List<PromocionDto> listar() {
        return repository.findAll().stream()
                .filter(this::promocionValida)
                .collect(Collectors.toList());
    }

    public PromocionDto consultar(String id) {
        return repository.findById(id);
    }

    public PromocionDto crear(PromocionDto dto) {
        validar(dto);
        return repository.create(dto);
    }

    public PromocionDto modificar(String id, PromocionDto dto) {
        validar(dto);
        return repository.update(id, dto);
    }

    public void eliminar(String id) {
        repository.delete(id);
    }

    private void validar(PromocionDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos de la promoción no pueden ser nulos.");
        }
        if (dto.getPorcentajeDescuento() <= 0 || dto.getPorcentajeDescuento() > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100.");
        }
        if (dto.getCodigoDescuento() <= 0) {
            throw new IllegalArgumentException("El código de descuento debe ser un número positivo.");
        }
        if (!StringUtils.hasText(dto.getDescripcionDescuento())) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }
        if (dto.getFechaDesde() == null || dto.getFechaHasta() == null) {
            throw new IllegalArgumentException("Debe indicar el período de vigencia.");
        }
        if (dto.getFechaHasta().isBefore(dto.getFechaDesde())) {
            throw new IllegalArgumentException("La fecha fin no puede ser anterior a la fecha de inicio.");
        }
    }

    private boolean promocionValida(PromocionDto dto) {
        if (dto == null) {
            return false;
        }
        LocalDate desde = dto.getFechaDesde();
        LocalDate hasta = dto.getFechaHasta();
        return desde != null && hasta != null && StringUtils.hasText(dto.getDescripcionDescuento());
    }
}

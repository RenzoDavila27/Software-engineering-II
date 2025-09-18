package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.ValorCuota;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.ValorCuotaRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ValorCuotaService {

    private final ValorCuotaRepository valorCuotaRepository;

    public ValorCuotaService(ValorCuotaRepository valorCuotaRepository) {
        this.valorCuotaRepository = valorCuotaRepository;
    }

    public ValorCuota crearValorCuota(LocalDate fechaDesde, LocalDate fechaHasta, double valorCuota) {
        validar(fechaDesde, fechaHasta, valorCuota);
        ValorCuota valor = new ValorCuota();
        valor.setFechaDesde(fechaDesde);
        valor.setFechaHasta(fechaHasta);
        valor.setValorCuota(valorCuota);
        return valorCuotaRepository.save(valor);
    }

    public void validar(LocalDate fechaDesde, LocalDate fechaHasta, double valorCuota) {
        if (fechaDesde == null) {
            throw new BusinessException("La fecha desde es obligatoria");
        }
        if (fechaHasta != null && fechaHasta.isBefore(fechaDesde)) {
            throw new BusinessException("La fecha hasta no puede ser anterior a la fecha desde");
        }
        if (valorCuota <= 0) {
            throw new BusinessException("El valor de la cuota debe ser mayor a cero");
        }
    }

    @Transactional(readOnly = true)
    public ValorCuota buscarValorCuota(String id) {
        return valorCuotaRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Valor de cuota no encontrado"));
    }

    public ValorCuota modificarValorCuota(String id, LocalDate fechaDesde, LocalDate fechaHasta, double valorCuota) {
        ValorCuota valor = buscarValorCuota(id);
        if (fechaDesde != null) {
            valor.setFechaDesde(fechaDesde);
        }
        if (fechaHasta != null || fechaDesde != null) {
            if (fechaHasta != null && fechaDesde == null && fechaHasta.isBefore(valor.getFechaDesde())) {
                throw new BusinessException("La fecha hasta no puede ser anterior a la fecha desde");
            }
            valor.setFechaHasta(fechaHasta);
        }
        if (valorCuota > 0) {
            valor.setValorCuota(valorCuota);
        }
        validar(valor.getFechaDesde(), valor.getFechaHasta(), valor.getValorCuota());
        return valorCuotaRepository.save(valor);
    }

    public void eliminarValorCuota(String id) {
        ValorCuota valor = buscarValorCuota(id);
        valor.setEliminado(true);
        valorCuotaRepository.save(valor);
    }

    @Transactional(readOnly = true)
    public List<ValorCuota> listarValorCuota() {
        return valorCuotaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ValorCuota> listarValorCuotaActivo() {
        return valorCuotaRepository.findAll().stream()
            .filter(v -> !v.isEliminado())
            .toList();
    }

    @Transactional(readOnly = true)
    public ValorCuota buscarValorCuotaVigente() {
        LocalDate hoy = LocalDate.now();
        return valorCuotaRepository.findAll().stream()
            .filter(v -> !v.isEliminado())
            .filter(v -> !v.getFechaDesde().isAfter(hoy))
            .filter(v -> v.getFechaHasta() == null || !v.getFechaHasta().isBefore(hoy))
            .max(Comparator.comparing(ValorCuota::getFechaDesde))
            .orElseThrow(() -> new BusinessException("No hay valores de cuota vigentes"));
    }
}

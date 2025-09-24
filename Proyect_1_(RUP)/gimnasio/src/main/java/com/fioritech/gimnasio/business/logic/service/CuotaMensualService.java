package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.CuotaMensual;
import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.ValorCuota;
import com.fioritech.gimnasio.business.domain.enums.EstadoCuotaMensual;
import com.fioritech.gimnasio.business.domain.enums.Mes;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.CuotaMensualRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuotaMensualService {

    private final CuotaMensualRepository cuotaMensualRepository;
    private final SocioService socioService;
    private final ValorCuotaService valorCuotaService;

    public CuotaMensualService(CuotaMensualRepository cuotaMensualRepository, SocioService socioService,
        ValorCuotaService valorCuotaService) {
        this.cuotaMensualRepository = cuotaMensualRepository;
        this.socioService = socioService;
        this.valorCuotaService = valorCuotaService;
    }

    public CuotaMensual crearCuota(String idSocio, Mes mes, Long anio, String idValorCuota) {
        validar(mes, anio, idValorCuota);
        Socio socio = socioService.buscarSocio(idSocio);
       //ValorCuota valorCuota = valorCuotaService.buscarValorCuota(idValorCuota);
        CuotaMensual cuota = new CuotaMensual();
        cuota.setSocio(socio);
        cuota.setMes(mes);
        cuota.setAnio(anio);
        cuota.setEstado(EstadoCuotaMensual.PENDIENTE);
        //cuota.setValorCuota(valorCuota);
        cuota.setFechaVencimiento(calcularVencimiento(mes, anio));
        return cuotaMensualRepository.save(cuota);
    }

    public void validar(Mes mes, Long anio, String idValorCuota) {
        if (mes == null) {
            throw new BusinessException("El mes es obligatorio");
        }
        if (anio == null || anio < 2000) {
            throw new BusinessException("El anio de la cuota es invalido");
        }
        if (idValorCuota == null || idValorCuota.isBlank()) {
            throw new BusinessException("Debe especificarse un valor de cuota");
        }
    }

    private LocalDate calcularVencimiento(Mes mes, Long anio) {
        YearMonth yearMonth = YearMonth.of(Math.toIntExact(anio), mes.ordinal() + 1);
        return yearMonth.atEndOfMonth();
    }

    @Transactional(readOnly = true)
    public CuotaMensual buscarCuotaMensual(String id) {
        return cuotaMensualRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Cuota mensual no encontrada"));
    }

    public CuotaMensual modificarCuota(String id, String idSocio, Mes mes, Long anio, String idValorCuota,
        EstadoCuotaMensual estado) {
        CuotaMensual cuota = buscarCuotaMensual(id);
        if (idSocio != null && !idSocio.isBlank()) {
            cuota.setSocio(socioService.buscarSocio(idSocio));
        }
        if (mes != null) {
            cuota.setMes(mes);
        }
        if (anio != null) {
            cuota.setAnio(anio);
        }
        if (idValorCuota != null && !idValorCuota.isBlank()) {
            //cuota.setValorCuota(valorCuotaService.buscarValorCuota(idValorCuota));
        }
        if (estado != null) {
            cuota.setEstado(estado);
        }
        cuota.setFechaVencimiento(calcularVencimiento(cuota.getMes(), cuota.getAnio()));
        return cuotaMensualRepository.save(cuota);
    }

    public void eliminarCuotaMensual(String id) {
        CuotaMensual cuota = buscarCuotaMensual(id);
        cuota.setEliminado(true);
        cuotaMensualRepository.save(cuota);
    }

    @Transactional(readOnly = true)
    public List<CuotaMensual> listarCuotaMensual() {
        return cuotaMensualRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CuotaMensual> listarCuotaMensualActiva() throws BusinessException{
        try{
            return cuotaMensualRepository.listarCuotaMensualActiva();
        } catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("Error de sistema");
        }
    }

    @Transactional(readOnly = true)
    public List<CuotaMensual> listarCuotaMensualPorEstado(EstadoCuotaMensual estado) {
        return cuotaMensualRepository.findAll().stream()
            .filter(c -> !c.isEliminado())
            .filter(c -> estado == null || c.getEstado() == estado)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CuotaMensual> listarCuotaMensualPorFecha(LocalDate fechaDesde, LocalDate fechaHasta) {
        return cuotaMensualRepository.findAll().stream()
            .filter(c -> !c.isEliminado())
            .filter(c -> {
                LocalDate vencimiento = c.getFechaVencimiento();
                boolean desde = fechaDesde == null || !vencimiento.isBefore(fechaDesde);
                boolean hasta = fechaHasta == null || !vencimiento.isAfter(fechaHasta);
                return desde && hasta;
            })
            .toList();
    }
}

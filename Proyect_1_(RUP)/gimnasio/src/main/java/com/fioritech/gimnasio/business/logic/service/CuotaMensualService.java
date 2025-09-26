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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuotaMensualService {

    private final CuotaMensualRepository cuotaMensualRepository;
    private final ValorCuotaService valorCuotaService;

    public CuotaMensualService(CuotaMensualRepository cuotaMensualRepository,ValorCuotaService valorCuotaService) {
        this.cuotaMensualRepository = cuotaMensualRepository;
        this.valorCuotaService = valorCuotaService;
    }

    @Transactional
    public void generarPrimeraCuotaParaNuevoSocio(Socio socio) {
        ValorCuota valorCuotaActual = valorCuotaService.ultimaCuota();

        LocalDate fechaActual = LocalDate.now();

        CuotaMensual cuota = new CuotaMensual();
        cuota.setSocio(socio);
        cuota.setValorCuota(valorCuotaActual);
        cuota.setMes(Mes.values()[fechaActual.getMonthValue() - 1]); // mes actual
        cuota.setAnio((long) fechaActual.getYear());
        cuota.setEstado(EstadoCuotaMensual.ADEUDADA);
        cuota.setFechaVencimiento(fechaActual.plusDays(30)); // ya es LocalDate

        cuotaMensualRepository.save(cuota);
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

    public CuotaMensual modificarCuota(String id, Mes mes, Long anio,EstadoCuotaMensual estado){
      CuotaMensual cuota = buscarCuotaMensual(id);
        if (mes != null) {
            cuota.setMes(mes);
        }
        if (anio != null) {
            cuota.setAnio(anio);
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
    @Transactional
    public Collection<CuotaMensual> listarDeudasPorSocio(String idSocio, EstadoCuotaMensual estado) {
        try{
            Collection<CuotaMensual> cuota = cuotaMensualRepository.listarDeudasPorSocio(idSocio,estado);
            return cuota;
        } catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("Error de sistema");
        }
    }

    public void actualizarCuotasMensuales(){
        Collection<CuotaMensual> cuotas = cuotaMensualRepository.ultimaCuotaDeSocio();
        LocalDate fechahoy = LocalDate.now();
        for (CuotaMensual cuota : cuotas){
            if (cuota.getFechaVencimiento()==fechahoy){
                generarNuevaCuotaMensual(cuota);
            }
        }
    }

    @Transactional
    public void generarNuevaCuotaMensual(CuotaMensual ultima_cuota){
        CuotaMensual nueva_cuota = new CuotaMensual();
        nueva_cuota.setSocio(ultima_cuota.getSocio());
        nueva_cuota.setValorCuota(ultima_cuota.getValorCuota());
        int siguienteMes = (ultima_cuota.getMes().ordinal() + 1) % Mes.values().length;
        nueva_cuota.setMes(Mes.values()[siguienteMes]);

        if (siguienteMes == 0) { 
            nueva_cuota.setAnio(ultima_cuota.getAnio() + 1);
        } else {
            nueva_cuota.setAnio(ultima_cuota.getAnio());
        }
        nueva_cuota.setEstado(EstadoCuotaMensual.ADEUDADA);
        nueva_cuota.setFechaVencimiento(ultima_cuota.getFechaVencimiento().plusMonths(1)); 

        cuotaMensualRepository.save(nueva_cuota);

    }

    public Collection<CuotaMensual> buscarCuotasDeSocioPorDNI(String dni){

        Collection<CuotaMensual> cuotas = cuotaMensualRepository.buscarCuotasDeSocioPorDNI(dni);
        return cuotas;
    }

    public Collection<CuotaMensual> listarCuotaMensualPorUsuario(String idUsuario){
         Collection<CuotaMensual> cuotas = cuotaMensualRepository.buscarCuotasDeSocioPorUsuario(idUsuario);
        return cuotas;
    }



}

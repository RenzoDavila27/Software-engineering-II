package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Promocion;
import com.fioritech.demo.bussines.domain.PromocionTipo;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PromocionScheduler {

    private final PromocionService promocionService;

    public PromocionScheduler(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @Scheduled(cron = "0 35 22 * * *")
    public void enviarPromocionGeneral() {
        enviarSegunTipo(PromocionTipo.PROMOCION_GENERAL);
    }

    @Scheduled(cron = "0 0 10 31 12 *")
    public void enviarSaludoFinAnio() {
        enviarSegunTipo(PromocionTipo.SALUDO_FIN_ANIO);
    }

    private void enviarSegunTipo(PromocionTipo tipo) {
        Optional<Promocion> promocionOpt = promocionService.obtenerPromocionPorTipo(tipo);
        if (promocionOpt.isEmpty()) {
            return;
        }
        Promocion promocion = promocionOpt.get();
        try {
            promocionService.enviarPromocionProgramada(promocion);
        } catch (BusinessException ex) {
            throw new BusinessException("No se pudo enviar la promocion programada: " + ex.getMessage(), ex);
        }
    }
}

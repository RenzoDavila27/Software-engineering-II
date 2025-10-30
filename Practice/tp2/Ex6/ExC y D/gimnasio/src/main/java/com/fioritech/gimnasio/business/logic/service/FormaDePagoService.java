package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.FormaDePago;
import com.fioritech.gimnasio.business.domain.enums.TipoPago;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.FormaDePagoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FormaDePagoService {

    private final FormaDePagoRepository formaDePagoRepository;

    public FormaDePagoService(FormaDePagoRepository formaDePagoRepository) {
        this.formaDePagoRepository = formaDePagoRepository;
    }

    public FormaDePago crearFormaDePago(TipoPago tipoPago, String observacion) {
        if (tipoPago == null) {
            throw new BusinessException("El tipo de pago es obligatorio");
        }
        FormaDePago formaDePago = new FormaDePago();
        formaDePago.setTipoPago(tipoPago);
        formaDePago.setObservacion(observacion);
        return formaDePagoRepository.save(formaDePago);
    }

    public FormaDePago modificarFormaDePago(String id, TipoPago tipoPago, String observacion) {
        FormaDePago formaDePago = buscarFormaDePago(id);
        if (tipoPago != null) {
            formaDePago.setTipoPago(tipoPago);
        }
        formaDePago.setObservacion(observacion);
        return formaDePagoRepository.save(formaDePago);
    }

    public void eliminarFormaDePago(String id) {
        FormaDePago formaDePago = buscarFormaDePago(id);
        formaDePago.setEliminado(true);
        formaDePagoRepository.save(formaDePago);
    }

    @Transactional(readOnly = true)
    public FormaDePago buscarFormaDePago(String id) {
        return formaDePagoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Forma de pago no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<FormaDePago> listarFormasDePago() {
        return formaDePagoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<FormaDePago> listarFormasDePagoActivas() {
        return formaDePagoRepository.findAll().stream()
            .filter(f -> !f.isEliminado())
            .toList();
    }

    @Transactional(readOnly = true)
    public FormaDePago buscarPorTipo(TipoPago tipoPago) {
        if (tipoPago == null) {
            throw new BusinessException("Debe indicar un tipo de pago");
        }
        return formaDePagoRepository.findFirstByTipoPagoAndEliminadoFalse(tipoPago)
            .orElseThrow(() -> new BusinessException("No hay una forma de pago activa para el tipo especificado"));
    }
}

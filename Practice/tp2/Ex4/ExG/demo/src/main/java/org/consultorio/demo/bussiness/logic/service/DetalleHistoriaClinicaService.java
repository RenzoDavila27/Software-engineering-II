package org.consultorio.demo.bussiness.logic.service;

import lombok.AllArgsConstructor;
import org.consultorio.demo.bussiness.domain.DetalleHistoriaClinica;
import org.consultorio.demo.bussiness.persistance.DetalleHistoriaClinicaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@AllArgsConstructor
public class DetalleHistoriaClinicaService extends TemplateService<DetalleHistoriaClinica> {

    @Autowired
    private final DetalleHistoriaClinicaRepository detalleHistoriaClinicaRepository;

    @Override
    protected JpaRepository<DetalleHistoriaClinica, String> getRepository() {
        return detalleHistoriaClinicaRepository;
    }
}

package org.consultorio.demo.bussiness.logic.service;

import lombok.AllArgsConstructor;
import org.consultorio.demo.bussiness.domain.HistoriaClinica;
import org.consultorio.demo.bussiness.persistance.HistoriaClinicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HistoriaClinicaService extends TemplateService<HistoriaClinica> {

    @Autowired
    private HistoriaClinicaRepository historiaClinicaRepository;

    @Override
    protected JpaRepository<HistoriaClinica, String> getRepository() {
        return historiaClinicaRepository;
    }
}

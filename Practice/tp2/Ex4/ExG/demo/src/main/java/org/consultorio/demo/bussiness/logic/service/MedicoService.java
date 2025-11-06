package org.consultorio.demo.bussiness.logic.service;

import lombok.AllArgsConstructor;
import org.consultorio.demo.bussiness.domain.Medico;
import org.consultorio.demo.bussiness.persistance.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MedicoService extends TemplateService<Medico> {

    @Autowired
    private MedicoRepository medicoRepository;

    @Override
    protected JpaRepository<Medico, String> getRepository() {
        return medicoRepository;
    }
}

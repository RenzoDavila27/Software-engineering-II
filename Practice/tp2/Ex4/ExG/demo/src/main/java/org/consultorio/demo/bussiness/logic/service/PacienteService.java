package org.consultorio.demo.bussiness.logic.service;

import lombok.AllArgsConstructor;
import org.consultorio.demo.bussiness.domain.Paciente;
import org.consultorio.demo.bussiness.persistance.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PacienteService extends TemplateService<Paciente> {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    protected JpaRepository<Paciente, String> getRepository() {
        return pacienteRepository;
    }
}

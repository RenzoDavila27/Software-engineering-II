package org.consultorio.demo.bussiness.logic.service;

import lombok.AllArgsConstructor;
import org.consultorio.demo.bussiness.domain.FotoPaciente;
import org.consultorio.demo.bussiness.persistance.FotoPacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FotoPacienteService extends TemplateService<FotoPaciente> {

    @Autowired
    private FotoPacienteRepository fotoPacienteRepository;

    @Override
    protected JpaRepository<FotoPaciente, String> getRepository() {
        return fotoPacienteRepository;
    }

    public FotoPaciente buscarPorUsuarioId(String usuarioId) {
        return fotoPacienteRepository.findByPacienteId(usuarioId);
    }
}

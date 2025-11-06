package org.consultorio.demo.bussiness.persistance;

import org.consultorio.demo.bussiness.domain.FotoPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoPacienteRepository extends JpaRepository<FotoPaciente, String> {

    @Query("SELECT f FROM FotoPaciente f WHERE f.paciente.id = ?1")
    FotoPaciente findByPacienteId(String pacienteId);

}

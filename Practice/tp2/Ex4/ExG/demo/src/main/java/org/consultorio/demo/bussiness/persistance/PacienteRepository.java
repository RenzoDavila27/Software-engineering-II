package org.consultorio.demo.bussiness.persistance;

import org.consultorio.demo.bussiness.domain.Paciente;
import org.consultorio.demo.bussiness.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, String> {

    @Query("SELECT p FROM Paciente p WHERE p.eliminado = false")
    List<Paciente> listAllActives();

}

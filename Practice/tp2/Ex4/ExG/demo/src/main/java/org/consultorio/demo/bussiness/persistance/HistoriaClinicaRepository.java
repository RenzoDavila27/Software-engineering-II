package org.consultorio.demo.bussiness.persistance;

import org.consultorio.demo.bussiness.domain.HistoriaClinica;
import org.consultorio.demo.bussiness.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, String> {

    @Query("SELECT h FROM HistoriaClinica h WHERE h.eliminado = false")
    List<HistoriaClinica> listAllActives();

    @Query("SELECT h FROM HistoriaClinica h WHERE h.usuario.id = ?1 and h.eliminado = false")
    List<HistoriaClinica> findByUsuarioId(String usuarioId);

    @Query("SELECT h FROM HistoriaClinica h WHERE h.paciente.id = ?1 and h.eliminado = false")
    List<HistoriaClinica> findByPacienteId(String pacienteId);


}

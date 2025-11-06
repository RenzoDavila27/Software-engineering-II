package org.consultorio.demo.bussiness.persistance;

import org.consultorio.demo.bussiness.domain.Medico;
import org.consultorio.demo.bussiness.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, String> {

    @Query("SELECT m FROM Medico m WHERE m.eliminado = false")
    List<Medico> listAllActives();

}

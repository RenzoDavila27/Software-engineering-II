package org.consultorio.demo.bussiness.persistance;

import org.consultorio.demo.bussiness.domain.DetalleHistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleHistoriaClinicaRepository extends JpaRepository<DetalleHistoriaClinica, String> {
}

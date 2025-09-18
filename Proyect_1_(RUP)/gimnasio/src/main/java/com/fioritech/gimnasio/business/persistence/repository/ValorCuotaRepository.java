package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.ValorCuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorCuotaRepository extends JpaRepository<ValorCuota, String> {
}

package com.car.business.percistence.repository;

import com.car.business.domain.Cliente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends BaseRepository<Cliente, String> {

    @Query("SELECT c FROM Cliente c WHERE c.id = ( SELECT u.persona.id FROM Usuario u WHERE u.id = ?1)")
    Optional<Cliente> findByUserId(String id);

}

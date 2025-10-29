package com.example.mecanic.bussines.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.mecanic.bussines.domain.entity.Cliente;

public interface ClienteRepository extends BaseRepository<Cliente,Long> {
    
    @Query("SELECT c FROM Cliente c WHERE c.documento = :documento AND c.eliminado = false")
    public Cliente buscarClientePorDocumento(@Param("documento")String documento);


}

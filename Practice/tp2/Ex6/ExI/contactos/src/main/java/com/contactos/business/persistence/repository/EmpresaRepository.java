package com.contactos.business.persistence.repository;

import java.util.List;

import com.contactos.business.domain.Empresa;
import com.contactos.business.domain.Persona;

public interface EmpresaRepository extends BaseRepository<Empresa, Long> {

    List<Empresa> findByPersonaAndEliminadoFalse(Persona persona);
}

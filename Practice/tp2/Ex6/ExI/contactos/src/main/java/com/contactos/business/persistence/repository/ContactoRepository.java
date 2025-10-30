package com.contactos.business.persistence.repository;

import java.util.List;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.Persona;

public interface ContactoRepository extends BaseRepository<Contacto, Long> {

    List<Contacto> findByPersonaAndEliminadoFalse(Persona persona);
}

package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}

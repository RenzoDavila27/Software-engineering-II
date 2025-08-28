package com.example.demo.business.persistence.repository;

import java.util.Collection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;


import com.example.demo.business.domain.Estudio;


public interface EstudioRepository extends JpaRepository<Estudio, Long> {
    
    	@Query("SELECT p FROM Estudio p WHERE p.nombre = :nombre AND p.activo = TRUE")
	public Estudio buscarEstudioPorNombre(@Param("nombre")String nombre);
        
        @Query("SELECT v FROM Estudio v WHERE v.id = :id AND v.activo = TRUE")
        public Estudio buscarEstudioPorId(@Param("id")Long id);
	
	@Query("SELECT p FROM Estudio p WHERE p.activo = TRUE")
	public Collection<Estudio> listarEstudioActivo();
    
}

package com.example.demo.business.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

import com.example.demo.business.domain.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria,Long> {

    @Query("SELECT c FROM Categoria c WHERE c.nombre = :nombre AND c.activo = TRUE")
    public Categoria buscarCategoriaPorNombre(@Param("nombre")String nombre);

    @Query("SELECT c FROM Categoria c WHERE c.id = :id AND c.activo = TRUE")
    public Categoria buscarCategoriaPorId(@Param("id")Long id);

    @Query("SELECT c FROM Categoria c WHERE c.activo = TRUE")
    public Collection<Categoria> listarCategoriaActivo();

}

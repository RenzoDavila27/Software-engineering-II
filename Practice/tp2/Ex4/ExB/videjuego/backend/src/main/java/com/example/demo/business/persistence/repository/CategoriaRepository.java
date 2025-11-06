package com.example.demo.business.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

import com.example.demo.business.domain.Categoria;

public interface CategoriaRepository extends BaseRepository<Categoria> {

    @Query("SELECT c FROM Categoria c WHERE c.nombre = :nombre AND c.eliminado = FALSE")
    public Categoria buscarCategoriaPorNombre(@Param("nombre")String nombre);

    @Query("SELECT c FROM Categoria c WHERE c.id = :id AND c.eliminado = FALSE")
    public Categoria buscarCategoriaPorId(@Param("id")Long id);

    @Query("SELECT c FROM Categoria c WHERE c.eliminado = FALSE")
    public Collection<Categoria> listarCategoriaActivo();

}

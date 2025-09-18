package com.example.alquiler.service;

import com.example.alquiler.entity.Inquilino;

import java.util.List;
import java.util.Optional;

public interface InquilinoService {
    List<Inquilino> listarActivos();
    Inquilino guardar(Inquilino inquilino);
    Optional<Inquilino> buscarPorId(Long id);
    void eliminarLogico(Long id); // marca eliminado = true
    void eliminarFisico(Long id); // borra de BD
}
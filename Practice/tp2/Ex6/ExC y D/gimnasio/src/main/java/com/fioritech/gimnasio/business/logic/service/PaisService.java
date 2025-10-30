package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Pais;
import com.fioritech.gimnasio.business.domain.Provincia;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.PaisRepository;
import com.fioritech.gimnasio.business.persistence.repository.ProvinciaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaisService {

    @Autowired
    private ProvinciaRepository provinciaRepository;

    private final PaisRepository paisRepository;

    public PaisService(PaisRepository paisRepository) {
        this.paisRepository = paisRepository;
    }

    @Transactional
    public Pais crearPais(String nombre) {
        validar(nombre);

        Optional<Pais> eliminado = paisRepository.findAll().stream()
            .filter(Pais::isEliminado)
            .filter(p -> p.getNombre().trim().equalsIgnoreCase(nombre.trim()))
            .findFirst();

        if (eliminado.isPresent()) {
            Pais pais = eliminado.get();
            pais.setEliminado(false);
            return paisRepository.save(pais);
        }

        Pais nuevo = new Pais();
        nuevo.setNombre(nombre.trim());
        return paisRepository.save(nuevo);
    }

    public void validar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre del pais es obligatorio");
        }
        String normalized = nombre.trim().toLowerCase(Locale.ROOT);
        boolean existe = paisRepository.findAll().stream()
            .filter(p -> !p.isEliminado())
            .map(p -> p.getNombre().trim().toLowerCase(Locale.ROOT))
            .anyMatch(normalized::equals);
        if (existe) {
            throw new BusinessException("Ya existe un pais con el mismo nombre");
        }
    }

    @Transactional(readOnly = true)
    public Pais buscarPais(String id) {
        return paisRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Pais no encontrado"));
    }

    @Transactional(readOnly = true)
    public Pais buscarPaisPorNombre(String nombre) {
        String normalized = nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT);
        return paisRepository.findAll().stream()
            .filter(p -> !p.isEliminado())
            .filter(p -> p.getNombre().trim().toLowerCase(Locale.ROOT).equals(normalized))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Pais no encontrado"));
    }

    public Pais modificarPais(String id, String nombre) {
        Pais pais = buscarPais(id);
        if (pais.isEliminado()) {
            throw new BusinessException("No se puede modificar un pais eliminado");
        }
        if (nombre != null && !nombre.isBlank() && !pais.getNombre().equalsIgnoreCase(nombre.trim())) {
            validar(nombre);
            pais.setNombre(nombre.trim());
        }
        return paisRepository.save(pais);
    }

    public void eliminarPais(String id) {
        Pais pais = buscarPais(id);
        pais.setEliminado(true);
        paisRepository.save(pais);
        Collection<Provincia> provincias = provinciaRepository.listarProvinciaPorPais(id);
        for (Provincia provincia : provincias) { 
            if (!provincia.isEliminado()) {       
                provincia.setEliminado(true);
                provinciaRepository.save(provincia);
            }
        }

    }

    @Transactional(readOnly = true)
    public List<Pais> listarPais() {
        return paisRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pais> listarPaisActivo() {
        return paisRepository.findAll().stream()
            .filter(p -> !p.isEliminado())
            .toList();
    }
}

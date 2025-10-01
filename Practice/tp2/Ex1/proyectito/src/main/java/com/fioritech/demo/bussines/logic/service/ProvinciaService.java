package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.PaisRepository;
import com.fioritech.demo.bussines.repository.ProvinciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class ProvinciaService {

    private final ProvinciaRepository provinciaRepository;
    private final PaisRepository paisRepository;

    public ProvinciaService(ProvinciaRepository provinciaRepository, PaisRepository paisRepository) {
        this.provinciaRepository = provinciaRepository;
        this.paisRepository = paisRepository;
    }

    public Provincia crearProvincia(Provincia provincia) {
        verificarAtributos(provincia);
        if (provincia.getId() != null) {
            throw new BusinessException("La provincia ya tiene un id asignado");
        }
        Pais pais = obtenerPaisActivo(provincia.getPais().getId());
        provincia.setNombre(provincia.getNombre().trim());
        provincia.setPais(pais);
        provincia.setEliminado(false);
        return provinciaRepository.save(provincia);
    }

    public Provincia modificarProvincia(Long id, Provincia cambios) {
        Provincia existente = obtenerProvinciaActiva(id);
        verificarAtributos(cambios);
        Pais pais = obtenerPaisActivo(cambios.getPais().getId());
        existente.setNombre(cambios.getNombre().trim());
        existente.setPais(pais);
        return provinciaRepository.save(existente);
    }

    public void eliminarProvincia(Long id) {
        Provincia existente = obtenerProvinciaActiva(id);
        existente.setEliminado(true);
        provinciaRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Collection<Provincia> listarProvincias() {
        return provinciaRepository.buscarProvinciasActivas();
    }

    @Transactional(readOnly = true)
    public Provincia buscarProvinciaPorId(Long id) {
        return obtenerProvinciaActiva(id);
    }

    @Transactional(readOnly = true)
    public Collection<Provincia> obtenerProvinciasActivas() {
        return provinciaRepository.buscarProvinciasActivas();
    }

    public void verificarAtributos(Provincia provincia) {
        if (provincia == null) {
            throw new BusinessException("La provincia es obligatoria");
        }
        if (ValidationUtils.isBlank(provincia.getNombre())) {
            throw new BusinessException("El nombre de la provincia es obligatorio");
        }
        if (provincia.getPais() == null || provincia.getPais().getId() == null) {
            throw new BusinessException("La provincia debe tener un pais asociado");
        }
    }

    private Pais obtenerPaisActivo(Long paisId) {
        Pais pais = paisRepository.findById(paisId)
                .orElseThrow(() -> new EntityNotFoundException("No existe el pais con id " + paisId));
        if (pais.isEliminado()) {
            throw new BusinessException("El pais con id " + paisId + " esta eliminado");
        }
        return pais;
    }

    private Provincia obtenerProvinciaActiva(Long id) {
        Provincia provincia = provinciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe la provincia con id " + id));
        if (provincia.isEliminado()) {
            throw new BusinessException("La provincia con id " + id + " esta eliminada");
        }
        return provincia;
    }
}

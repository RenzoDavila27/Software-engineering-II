package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.template.CrudTemplateService;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.PaisRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class PaisService extends CrudTemplateService<Pais, Long> {

    private final PaisRepository paisRepository;

    public PaisService(PaisRepository paisRepository) {
        this.paisRepository = paisRepository;
    }

    public Pais crearPais(Pais pais) {
        return crearEntidad(pais);
    }

    public Pais modificarPais(Long id, Pais cambios) {
        return modificarEntidad(id, cambios);
    }

    public void eliminarPais(Long id) {
        eliminarEntidad(id);
    }

    @Transactional(readOnly = true)
    public Collection<Pais> listarPaises() {
        return listarEntidades();
    }

    @Transactional(readOnly = true)
    public Pais buscarPaisPorId(Long id) {
        return buscarEntidad(id);
    }

    public void verificarAtributos(Pais pais) {
        if (pais == null) {
            throw new BusinessException("El pais es obligatorio");
        }
        if (ValidationUtils.isBlank(pais.getNombre())) {
            throw new BusinessException("El nombre del pais es obligatorio");
        }
    }

    @Override
    protected void validarEntidad(Pais pais) {
        verificarAtributos(pais);
    }

    @Override
    protected void validarEntidadNueva(Pais pais) {
        if (pais.getId() != null) {
            throw new BusinessException("El pais ya tiene un id asignado");
        }
    }

    @Override
    protected void antesDeCrear(Pais pais) {
        pais.setNombre(pais.getNombre().trim());
        pais.setEliminado(false);
    }

    @Override
    protected void aplicarCambios(Pais existente, Pais cambios) {
        existente.setNombre(cambios.getNombre().trim());
    }

    @Override
    protected void marcarEliminado(Pais pais) {
        pais.setEliminado(true);
    }

    @Override
    protected Pais guardar(Pais pais) {
        return paisRepository.save(pais);
    }

    @Override
    protected Pais obtenerPorId(Long id) {
        return obtenerPaisActivo(id);
    }

    @Override
    protected Collection<Pais> obtenerListado() {
        return paisRepository.buscarPaisesActivos();
    }

    private Pais obtenerPaisActivo(Long id) {
        Pais pais = paisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el pais con id " + id));
        if (pais.isEliminado()) {
            throw new BusinessException("El pais con id " + id + " esta eliminado");
        }
        return pais;
    }
}


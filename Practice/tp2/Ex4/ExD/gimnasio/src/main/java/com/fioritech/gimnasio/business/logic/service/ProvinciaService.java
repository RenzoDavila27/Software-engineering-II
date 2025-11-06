package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Pais;
import com.fioritech.gimnasio.business.domain.Provincia;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.ProvinciaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProvinciaService {

    private final ProvinciaRepository provinciaRepository;
    private final PaisService paisService;

    public ProvinciaService(ProvinciaRepository provinciaRepository, PaisService paisService) {
        this.provinciaRepository = provinciaRepository;
        this.paisService = paisService;
    }

    public Provincia crearProvincia(String nombre, String idPais) {
        Pais pais = paisService.buscarPais(idPais);
        validar(nombre, pais);
        Provincia provincia = new Provincia();
        provincia.setNombre(nombre.trim());
        provincia.setPais(pais);
        return provinciaRepository.save(provincia);
    }

    public void validar(String nombre, Pais pais) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre de la provincia es obligatorio");
        }
        if (pais == null || pais.isEliminado()) {
            throw new BusinessException("El pais asociado es obligatorio");
        }
        String normalized = nombre.trim().toLowerCase(Locale.ROOT);
        boolean existe = provinciaRepository.findAll().stream()
            .filter(p -> !p.isEliminado())
            .filter(p -> p.getPais().getId().equals(pais.getId()))
            .map(p -> p.getNombre().trim().toLowerCase(Locale.ROOT))
            .anyMatch(normalized::equals);
        if (existe) {
            throw new BusinessException("Ya existe una provincia con ese nombre en el pais seleccionado");
        }
    }

    @Transactional(readOnly = true)
    public Provincia buscarProvincia(String id) {
        return provinciaRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Provincia no encontrada"));
    }

    @Transactional(readOnly = true)
    public Provincia buscarProvinciaPorNombre(String nombre) {
        String normalized = nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT);
        return provinciaRepository.findAll().stream()
            .filter(p -> !p.isEliminado())
            .filter(p -> p.getNombre().trim().toLowerCase(Locale.ROOT).equals(normalized))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Provincia no encontrada"));
    }

    public Provincia modificarProvincia(String id, String nombre, String idPais) {
        Provincia provincia = buscarProvincia(id);
        if (provincia.isEliminado()) {
            throw new BusinessException("No se puede modificar una provincia eliminada");
        }
        Pais pais = provincia.getPais();
        if (idPais != null && !idPais.isBlank() && (pais == null || !pais.getId().equals(idPais))) {
            pais = paisService.buscarPais(idPais);
        }
        if (nombre != null && !nombre.isBlank()
            && !provincia.getNombre().equalsIgnoreCase(nombre.trim())) {
            validar(nombre, pais);
            provincia.setNombre(nombre.trim());
        }
        provincia.setPais(pais);
        return provinciaRepository.save(provincia);
    }

    public void eliminarProvincia(String id) {
        Provincia provincia = buscarProvincia(id);
        provincia.setEliminado(true);
        provinciaRepository.save(provincia);
    }

    @Transactional(readOnly = true)
    public List<Provincia> listarProvincia(String idPais) {
        return provinciaRepository.findAll().stream()
            .filter(p -> idPais == null || idPais.isBlank() || p.getPais().getId().equals(idPais))
            .toList();
    }

    @Transactional(readOnly = true)
    public Collection<Provincia> listarProvinciaActiva() {
        return provinciaRepository.listarProvinciaActiva();
    }

    
}

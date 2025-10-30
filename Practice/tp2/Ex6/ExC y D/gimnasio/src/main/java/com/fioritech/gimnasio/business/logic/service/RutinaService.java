package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.DetalleRutina;
import com.fioritech.gimnasio.business.domain.Empleado;
import com.fioritech.gimnasio.business.domain.Rutina;
import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.enums.EstadoDetalleRutina;
import com.fioritech.gimnasio.business.domain.enums.EstadoRutina;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.DetalleRutinaRepository;
import com.fioritech.gimnasio.business.persistence.repository.RutinaRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final DetalleRutinaRepository detalleRutinaRepository;
    private final EmpleadoService empleadoService;
    private final SocioService socioService;

    public RutinaService(RutinaRepository rutinaRepository, DetalleRutinaRepository detalleRutinaRepository,
        EmpleadoService empleadoService, SocioService socioService) {
        this.rutinaRepository = rutinaRepository;
        this.detalleRutinaRepository = detalleRutinaRepository;
        this.empleadoService = empleadoService;
        this.socioService = socioService;
    }

    public Rutina crearRutina(String idProfesor, String idSocio, LocalDate fechaInicio, LocalDate fechaFinalizacion,
        List<DetalleRutina> detalle) {
        Empleado profesor = empleadoService.buscarEmpleado(idProfesor);
        Socio socio = socioService.buscarSocio(idSocio);
        validar(idProfesor, idSocio, fechaInicio, fechaFinalizacion, detalle);
        Rutina rutina = new Rutina();
        rutina.setProfesor(profesor);
        rutina.setSocio(socio);
        rutina.setFechaInicio(fechaInicio);
        rutina.setFechaFinalizacion(fechaFinalizacion);
        rutina.setEstadoRutina(EstadoRutina.EN_PROCESO);
        List<DetalleRutina> detallesPersistentes = new ArrayList<>();
        if (detalle != null) {
            for (DetalleRutina det : detalle) {
                if (det.getId() != null && det.getId().isBlank()) {
                    det.setId(null);
                }
                det.setRutina(rutina);
                if (det.getEstadoDetalle() == null) {
                    det.setEstadoDetalle(EstadoDetalleRutina.SIN_REALIZAR);
                }
                detallesPersistentes.add(det);
            }
        }
        rutina.setDetalles(detallesPersistentes);
        return rutinaRepository.save(rutina);
    }

    public void validar(String idProfesor, String idSocio, LocalDate fechaInicio, LocalDate fechaFinalizacion,
        List<DetalleRutina> detalle) {
        if (idProfesor == null || idProfesor.isBlank()) {
            throw new BusinessException("El profesor es obligatorio");
        }
        if (idSocio == null || idSocio.isBlank()) {
            throw new BusinessException("El socio es obligatorio");
        }
        if (fechaInicio == null) {
            throw new BusinessException("La fecha de inicio es obligatoria");
        }
        if (fechaFinalizacion != null && fechaFinalizacion.isBefore(fechaInicio)) {
            throw new BusinessException("La fecha de finalizacion no puede ser anterior a la fecha de inicio");
        }
        if (detalle != null) {
            detalle.forEach(d -> {
                if (d.getFecha() == null) {
                    throw new BusinessException("Las actividades de la rutina deben tener fecha");
                }
                if (d.getActividad() == null || d.getActividad().isBlank()) {
                    throw new BusinessException("Las actividades de la rutina deben tener descripcion");
                }
            });
        }
    }

    public Rutina modificarRutina(String id, String idProfesor, String idSocio, LocalDate fechaInicio,
        LocalDate fechaFinalizacion, List<DetalleRutina> detalle) {
        Rutina rutina = buscarRutina(id);
        if (idProfesor != null && !idProfesor.isBlank()
            && !rutina.getProfesor().getId().equals(idProfesor)) {
            rutina.setProfesor(empleadoService.buscarEmpleado(idProfesor));
        }
        if (idSocio != null && !idSocio.isBlank() && !rutina.getSocio().getId().equals(idSocio)) {
            rutina.setSocio(socioService.buscarSocio(idSocio));
        }
        if (fechaInicio != null) {
            rutina.setFechaInicio(fechaInicio);
        }
        if (fechaFinalizacion != null) {
            rutina.setFechaFinalizacion(fechaFinalizacion);
        }
        if (detalle != null && !detalle.isEmpty()) {
            rutina.getDetalles().clear();
            for (DetalleRutina det : detalle) {
                if (det.getId() != null && det.getId().isBlank()) {
                    det.setId(null);
                }
                det.setRutina(rutina);
                if (det.getEstadoDetalle() == null) {
                    det.setEstadoDetalle(EstadoDetalleRutina.SIN_REALIZAR);
                }
                rutina.getDetalles().add(det);
            }
        }
        return rutinaRepository.save(rutina);
    }

    @Transactional(readOnly = true)
    public Rutina buscarRutina(String id) {
        return rutinaRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Rutina no encontrada"));
    }

    public void eliminarRutina(String id) {
        Rutina rutina = buscarRutina(id);
        rutina.setEliminado(true);
        rutinaRepository.save(rutina);
    }

    public Rutina modificarEstadoRutina(String id, EstadoRutina estadoRutina) {
        if (estadoRutina == null) {
            throw new BusinessException("El estado es obligatorio");
        }
        Rutina rutina = buscarRutina(id);
        rutina.setEstadoRutina(estadoRutina);
        return rutinaRepository.save(rutina);
    }

    @Transactional(readOnly = true)
    public List<Rutina> listarRutina() {
        return rutinaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Rutina> listarRutinaActivo() {
        return rutinaRepository.findAll().stream()
            .filter(r -> !r.isEliminado())
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Rutina> listarRutinaActivoPorSocio(String socioId) {
        if (socioId == null || socioId.isBlank()) {
            throw new BusinessException("El socio es obligatorio");
        }
        return rutinaRepository.findBySocioIdAndEliminadoFalse(socioId);
    }

    @Transactional(readOnly = true)
    public Rutina buscarRutinaActual() {
        return rutinaRepository.findAll().stream()
            .filter(r -> !r.isEliminado())
            .filter(r -> r.getEstadoRutina() == EstadoRutina.EN_PROCESO)
            .findFirst()
            .orElseThrow(() -> new BusinessException("No hay rutinas en curso"));
    }

    public DetalleRutina crearDetalleRutina(String idRutina, LocalDate fecha, String actividad) {
        if (fecha == null || actividad == null || actividad.isBlank()) {
            throw new BusinessException("Fecha y actividad son obligatorias");
        }
        Rutina rutina = buscarRutina(idRutina);
        DetalleRutina detalle = new DetalleRutina();
        detalle.setFecha(fecha);
        detalle.setActividad(actividad.trim());
        detalle.setEstadoDetalle(EstadoDetalleRutina.SIN_REALIZAR);
        detalle.setRutina(rutina);
        detalle = detalleRutinaRepository.save(detalle);
        rutina.getDetalles().add(detalle);
        return detalle;
    }

    @Transactional(readOnly = true)
    public DetalleRutina buscarDetalleRutina(String idDetalleRutina) {
        return detalleRutinaRepository.findById(idDetalleRutina)
            .orElseThrow(() -> new BusinessException("Detalle de rutina no encontrado"));
    }

    public DetalleRutina modificarDetalleRutina(String idDetalleRutina, LocalDate fecha, String actividad) {
        DetalleRutina detalle = buscarDetalleRutina(idDetalleRutina);
        if (fecha != null) {
            detalle.setFecha(fecha);
        }
        if (actividad != null && !actividad.isBlank()) {
            detalle.setActividad(actividad.trim());
        }
        return detalleRutinaRepository.save(detalle);
    }

    public void eliminarDetalleRutina(String idDetalleRutina) {
        DetalleRutina detalle = buscarDetalleRutina(idDetalleRutina);
        detalle.setEliminado(true);
        detalleRutinaRepository.save(detalle);
    }

    public DetalleRutina modificarEstadoDetalleRutina(String idDetalleRutina, EstadoDetalleRutina estadoDetalleRutina) {
        if (estadoDetalleRutina == null) {
            throw new BusinessException("El estado del detalle es obligatorio");
        }
        DetalleRutina detalle = buscarDetalleRutina(idDetalleRutina);
        detalle.setEstadoDetalle(estadoDetalleRutina);
        return detalleRutinaRepository.save(detalle);
    }
}

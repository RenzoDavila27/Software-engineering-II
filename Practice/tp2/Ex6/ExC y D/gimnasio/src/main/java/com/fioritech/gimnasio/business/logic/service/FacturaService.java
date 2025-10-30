package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.CuotaMensual;
import com.fioritech.gimnasio.business.domain.DetalleFactura;
import com.fioritech.gimnasio.business.domain.Factura;
import com.fioritech.gimnasio.business.domain.FormaDePago;
import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.enums.EstadoFactura;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.DetalleFacturaRepository;
import com.fioritech.gimnasio.business.persistence.repository.FacturaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;
    private final CuotaMensualService cuotaMensualService;
    private final FormaDePagoService formaDePagoService;
    private final SocioService socioService;

    public FacturaService(FacturaRepository facturaRepository, DetalleFacturaRepository detalleFacturaRepository,
        CuotaMensualService cuotaMensualService, FormaDePagoService formaDePagoService, SocioService socioService) {
        this.facturaRepository = facturaRepository;
        this.detalleFacturaRepository = detalleFacturaRepository;
        this.cuotaMensualService = cuotaMensualService;
        this.formaDePagoService = formaDePagoService;
        this.socioService = socioService;
    }

    public Factura crearFactura(Long numeroFactura, LocalDate fechaFactura, double totalPago, EstadoFactura estado,
        String idSocio, String idFormaDePago, List<String> idCuotasMensuales) {
        validar(numeroFactura, fechaFactura, totalPago, estado, idCuotasMensuales);
        validarNumeroFacturaUnico(numeroFactura, null);
        Socio socio = socioService.buscarSocio(idSocio);
        FormaDePago formaDePago = formaDePagoService.buscarFormaDePago(idFormaDePago);

        Factura factura = new Factura();
        factura.setNumeroFactura(numeroFactura);
        factura.setFechaFactura(fechaFactura);
        factura.setEstado(estado);
        factura.setSocio(socio);
        factura.setFormaDePago(formaDePago);
        factura.setTotalPagado(BigDecimal.valueOf(totalPago));

        Factura facturaPersistida = facturaRepository.save(factura);
        List<DetalleFactura> detalles = new ArrayList<>();
        for (String idCuota : idCuotasMensuales) {
            detalles.add(crearDetalleFacturaInterno(facturaPersistida, idCuota));
        }
        facturaPersistida.setDetalles(detalles);
        return facturaRepository.save(facturaPersistida);
    }

    public void validar(Long numeroFactura, LocalDate fechaFactura, double totalPago, EstadoFactura estado,
        List<String> idCuotasMensuales) {
        if (numeroFactura == null) {
            throw new BusinessException("El numero de factura es obligatorio");
        }
        if (fechaFactura == null) {
            throw new BusinessException("La fecha de la factura es obligatoria");
        }
        if (totalPago < 0) {
            throw new BusinessException("El total pagado no puede ser negativo");
        }
        if (estado == null) {
            throw new BusinessException("El estado de la factura es obligatorio");
        }
        if (idCuotasMensuales == null || idCuotasMensuales.isEmpty()) {
            throw new BusinessException("La factura debe contener al menos una cuota mensual");
        }
    }

    private void validarNumeroFacturaUnico(Long numeroFactura, String idActual) {
        String numeroNormalizado = numeroFactura.toString().trim().toLowerCase(Locale.ROOT);
        boolean existe = facturaRepository.findAll().stream()
            .filter(f -> !f.isEliminado())
            .filter(f -> idActual == null || !f.getId().equals(idActual))
            .map(f -> f.getNumeroFactura().toString().trim().toLowerCase(Locale.ROOT))
            .anyMatch(numeroNormalizado::equals);
        if (existe) {
            throw new BusinessException("Ya existe una factura con ese numero");
        }
    }

    @Transactional(readOnly = true)
    public Factura buscarFactura(String id) {
        return facturaRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Factura no encontrada"));
    }

    public Factura modificarFactura(String id, Long numeroFactura, LocalDate fechaFactura, Double totalPago,
        EstadoFactura estado, List<String> nuevosDetalleCuotas) {
        Factura factura = buscarFactura(id);
        if (numeroFactura != null && !factura.getNumeroFactura().equals(numeroFactura)) {
            validarNumeroFacturaUnico(numeroFactura, factura.getId());
            factura.setNumeroFactura(numeroFactura);
        }
        if (fechaFactura != null) {
            factura.setFechaFactura(fechaFactura);
        }
        if (totalPago != null) {
            if (totalPago < 0) {
                throw new BusinessException("El total pagado no puede ser negativo");
            }
            factura.setTotalPagado(BigDecimal.valueOf(totalPago));
        }
        if (estado != null) {
            factura.setEstado(estado);
        }
        if (nuevosDetalleCuotas != null) {
            List<DetalleFactura> actuales = new ArrayList<>(factura.getDetalles());
            actuales.forEach(detalleFacturaRepository::delete);
            factura.getDetalles().clear();
            for (String idCuota : nuevosDetalleCuotas) {
                factura.getDetalles().add(crearDetalleFacturaInterno(factura, idCuota));
            }
        }
        return facturaRepository.save(factura);
    }

    public void eliminarFactura(String id) {
        Factura factura = buscarFactura(id);
        factura.setEliminado(true);
        facturaRepository.save(factura);
    }

    @Transactional(readOnly = true)
    public List<Factura> listarFactura() {
        return facturaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Factura> listarFacturaActivo() {
        return facturaRepository.findAll().stream()
            .filter(f -> !f.isEliminado())
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Factura> listarFacturaPorUsuario(String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank()) {
            return List.of();
        }
        return facturaRepository.findBySocioUsuarioIdAndEliminadoFalse(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Factura> listarFacturaPorEstado(EstadoFactura estado) {
        return facturaRepository.findAll().stream()
            .filter(f -> !f.isEliminado())
            .filter(f -> estado == null || f.getEstado() == estado)
            .toList();
    }

    public DetalleFactura crearDetalleFactura(String idFactura, String idCuotaMensual) {
        Factura factura = buscarFactura(idFactura);
        DetalleFactura detalle = crearDetalleFacturaInterno(factura, idCuotaMensual);
        factura.getDetalles().add(detalle);
        facturaRepository.save(factura);
        return detalle;
    }

    private DetalleFactura crearDetalleFacturaInterno(Factura factura, String idCuotaMensual) {
        CuotaMensual cuotaMensual = cuotaMensualService.buscarCuotaMensual(idCuotaMensual);
        DetalleFactura detalle = new DetalleFactura();
        detalle.setFactura(factura);
        detalle.setCuotaMensual(cuotaMensual);
        detalle.setMonto(BigDecimal.valueOf(cuotaMensual.getValorCuota().getValorCuota()));
        return detalleFacturaRepository.save(detalle);
    }

    @Transactional(readOnly = true)
    public DetalleFactura buscarDetalleFactura(String idDetalleFactura) {
        return detalleFacturaRepository.findById(idDetalleFactura)
            .orElseThrow(() -> new BusinessException("Detalle de factura no encontrado"));
    }

    public DetalleFactura modificarDetalleFactura(String idDetalleFactura, String idCuotaMensual) {
        DetalleFactura detalle = buscarDetalleFactura(idDetalleFactura);
        if (idCuotaMensual != null && !idCuotaMensual.isBlank()) {
            CuotaMensual cuotaMensual = cuotaMensualService.buscarCuotaMensual(idCuotaMensual);
            detalle.setCuotaMensual(cuotaMensual);
            detalle.setMonto(BigDecimal.valueOf(cuotaMensual.getValorCuota().getValorCuota()));
        }
        return detalleFacturaRepository.save(detalle);
    }

    public void eliminarDetalleFactura(String idDetalleFactura) {
        DetalleFactura detalle = buscarDetalleFactura(idDetalleFactura);
        detalle.setEliminado(true);
        detalleFacturaRepository.save(detalle);
    }
}

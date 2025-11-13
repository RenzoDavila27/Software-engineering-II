package com.car.clientead.business.logic;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.car.clientead.business.logic.view.FacturaDetalleView;
import com.car.clientead.business.logic.view.report.FacturaLineaReport;
import com.car.clientead.client.dto.AlquilerDto;
import com.car.clientead.client.dto.ClienteDto;
import com.car.clientead.client.dto.DetalleFacturaDto;
import com.car.clientead.client.dto.FacturaDto;
import com.car.clientead.client.dto.PromocionDto;
import com.car.clientead.client.dto.VehiculoDto;
import com.car.clientead.client.exception.ApiClientException;
import com.car.clientead.repository.AlquilerRepository;
import com.car.clientead.repository.ClienteRepository;
import com.car.clientead.repository.DetalleFacturaRepository;
import com.car.clientead.repository.FacturaRepository;
import com.car.clientead.repository.PromocionRepository;
import com.car.clientead.repository.VehiculoRepository;

import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class FacturaService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private DetalleFacturaRepository detalleFacturaRepository;
    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private PromocionRepository promocionRepository;
    @Autowired
    private ResourceLoader resourceLoader;

    private JasperReport facturaReport;

    @PostConstruct
    public void compilarPlantilla() {
        try (InputStream inputStream = resourceLoader
                .getResource("classpath:jasper/factura.jrxml")
                .getInputStream()) {
            this.facturaReport = JasperCompileManager.compileReport(inputStream);
        } catch (IOException | JRException ex) {
            throw new IllegalStateException("No se pudo compilar la plantilla de factura.", ex);
        }
    }

    public Map<String, String> mapearFacturaPorAlquiler() {
        Map<String, String> resultado = new HashMap<>();
        List<FacturaDto> facturas = facturaRepository.findAll();
        Map<String, DetalleFacturaDto> detalleMap = cargarDetalles();
        for (FacturaDto factura : facturas) {
            if (factura.getDetalleIds() == null) {
                continue;
            }
            for (String detalleId : factura.getDetalleIds()) {
                DetalleFacturaDto detalle = detalleMap.get(detalleId);
                if (detalle != null && detalle.getAlquilerId() != null) {
                    resultado.put(detalle.getAlquilerId(), factura.getId());
                }
            }
        }
        return resultado;
    }

    public Optional<FacturaDetalleView> buscarPorAlquiler(String alquilerId) {
        if (alquilerId == null) {
            return Optional.empty();
        }
        List<FacturaDto> facturas = facturaRepository.findAll();
        Map<String, DetalleFacturaDto> detalleMap = cargarDetalles();
        for (FacturaDto factura : facturas) {
            if (factura.getDetalleIds() == null || factura.getDetalleIds().isEmpty()) {
                continue;
            }
            List<DetalleFacturaDto> detalles = factura.getDetalleIds().stream()
                    .map(detalleMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            boolean coincide = detalles.stream().anyMatch(d -> alquilerId.equals(d.getAlquilerId()));
            if (coincide) {
                return Optional.of(armarVista(factura, detalles, alquilerId));
            }
        }
        return Optional.empty();
    }

    public byte[] generarPdf(FacturaDetalleView vista) {
        if (vista == null || vista.getFactura() == null) {
            throw new ApiClientException("No hay datos de factura para generar el PDF.");
        }
        if (facturaReport == null) {
            throw new ApiClientException("La plantilla de facturas no está disponible.");
        }
        try {
            List<FacturaLineaReport> lineas = construirLineasReporte(vista);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lineas);
            Map<String, Object> parametros = construirParametros(vista);
            JasperPrint print = JasperFillManager.fillReport(facturaReport, parametros, dataSource);
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException ex) {
            throw new ApiClientException("No se pudo generar el PDF de la factura.", ex);
        }
    }

    private Map<String, DetalleFacturaDto> cargarDetalles() {
        return detalleFacturaRepository.findAll().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(DetalleFacturaDto::getId, d -> d));
    }

    private FacturaDetalleView armarVista(FacturaDto factura, List<DetalleFacturaDto> detalles, String alquilerId) {
        AlquilerDto alquiler = alquilerRepository.findById(alquilerId);
        ClienteDto cliente = alquiler != null && alquiler.getClienteId() != null
                ? clienteRepository.findById(alquiler.getClienteId())
                : null;
        VehiculoDto vehiculo = alquiler != null && alquiler.getVehiculoId() != null
                ? obtenerVehiculo(alquiler.getVehiculoId())
                : null;
        Map<String, PromocionDto> promociones = promocionRepository.findAll().stream()
                .collect(Collectors.toMap(PromocionDto::getId, p -> p));

        List<FacturaDetalleView.DetalleLinea> lineas = detalles.stream()
                .map(detalle -> new FacturaDetalleView.DetalleLinea(detalle,
                        detalle.getPromocionId() != null ? promociones.get(detalle.getPromocionId()) : null))
                .collect(Collectors.toList());

        return new FacturaDetalleView(factura, lineas, alquiler, cliente, vehiculo);
    }

    private VehiculoDto obtenerVehiculo(String vehiculoId) {
        try {
            return vehiculoRepository.findById(vehiculoId);
        } catch (ApiClientException ex) {
            return null;
        }
    }

    private Map<String, Object> construirParametros(FacturaDetalleView vista) {
        Map<String, Object> params = new HashMap<>();
        FacturaDto factura = vista.getFactura();
        ClienteDto cliente = vista.getCliente();
        VehiculoDto vehiculo = vista.getVehiculo();
        AlquilerDto alquiler = vista.getAlquiler();

        params.put("paramTitulo", "Factura #" + (factura.getNumeroFactura() != null ? factura.getNumeroFactura() : "-"));
        params.put("paramFecha", formatDate(factura.getFechaFactura()));
        params.put("paramCliente", cliente != null ? cliente.getNombre() + " " + cliente.getApellido() : "Cliente no informado");
        params.put("paramDocumento", cliente != null
                ? cliente.getTipoDocumento() + " " + cliente.getNumeroDocumento()
                : "-");
        params.put("paramVehiculo", vehiculo != null ? vehiculo.getPatente() : "Vehículo no informado");
        params.put("paramPeriodo", alquiler != null
                ? formatDate(alquiler.getFechaDesde()) + " - " + formatDate(alquiler.getFechaHasta())
                : "-");
        params.put("paramTotal", factura.getTotalPagado() != null ? factura.getTotalPagado() : 0d);
        return params;
    }

    private List<FacturaLineaReport> construirLineasReporte(FacturaDetalleView vista) {
        List<FacturaLineaReport> lineas = vista.getDetalles().stream()
                .map(detalle -> {
                    String descripcion = "Alquiler " + detalle.getDetalle().getAlquilerId();
                    if (detalle.getPromocion() != null) {
                        descripcion += " (" + detalle.getPromocion().getCodigoDescuento() + ")";
                    }
                    String promocion = detalle.getPromocion() != null
                            ? detalle.getPromocion().getDescripcionDescuento()
                            : "Sin promoción";
                    Double subtotal = detalle.getDetalle().getSubtotal();
                    if (subtotal == null) {
                        subtotal = 0d;
                    }
                    return new FacturaLineaReport(descripcion, promocion, subtotal);
                })
                .collect(Collectors.toList());
        if (lineas.isEmpty()) {
            return Collections.singletonList(new FacturaLineaReport("Sin cargos", "-", 0d));
        }
        return lineas;
    }

    private String formatDate(LocalDate date) {
        return date != null ? DATE_FORMATTER.format(date) : "-";
    }
}

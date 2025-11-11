package com.car.clientead.business.logic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.car.clientead.business.logic.view.FacturaDetalleView;
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

@Service
public class FacturaService {

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
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);

            float y = page.getMediaBox().getHeight() - 50;
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 18);
            content.newLineAtOffset(50, y);
            content.showText("Factura #" + vista.getFactura().getNumeroFactura());
            content.endText();

            y -= 30;
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(50, y);
            content.showText("Fecha: " + vista.getFactura().getFechaFactura());
            content.endText();

            y -= 18;
            if (vista.getCliente() != null) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                content.showText("Cliente: " + vista.getCliente().getNombre() + " " + vista.getCliente().getApellido());
                content.endText();
                y -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                content.showText("Documento: " + vista.getCliente().getTipoDocumento() + " " + vista.getCliente().getNumeroDocumento());
                content.endText();
                y -= 18;
            }

            if (vista.getVehiculo() != null) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                content.showText("Vehículo: " + vista.getVehiculo().getPatente());
                content.endText();
                y -= 18;
            }

            y -= 10;
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.newLineAtOffset(50, y);
            content.showText("Detalle");
            content.endText();
            y -= 16;

            for (FacturaDetalleView.DetalleLinea linea : vista.getDetalles()) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                String descripcion = "Alquiler " + linea.getDetalle().getAlquilerId();
                if (linea.getPromocion() != null) {
                    descripcion += " - Promo " + linea.getPromocion().getCodigoDescuento();
                }
                content.showText(descripcion);
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(400, y);
                content.showText(String.format(java.util.Locale.US, "$%.2f", linea.getDetalle().getSubtotal()));
                content.endText();
                y -= 18;
            }

            y -= 10;
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.newLineAtOffset(50, y);
            content.showText(String.format(java.util.Locale.US, "Total: $%.2f", vista.getFactura().getTotalPagado()));
            content.endText();

            content.close();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
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
}

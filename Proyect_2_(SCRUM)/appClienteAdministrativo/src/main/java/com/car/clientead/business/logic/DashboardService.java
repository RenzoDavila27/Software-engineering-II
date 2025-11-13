package com.car.clientead.business.logic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.business.logic.view.DashboardReportView;
import com.car.clientead.business.logic.view.ModeloRecaudacionView;
import com.car.clientead.business.logic.view.VehiculoAlquilerInfo;
import com.car.clientead.business.logic.view.VehiculoResumenView;
import com.car.clientead.client.dto.AlquilerDto;
import com.car.clientead.client.dto.CaracteristicaVehiculoDto;
import com.car.clientead.client.dto.ClienteDto;
import com.car.clientead.client.dto.DetalleFacturaDto;
import com.car.clientead.client.dto.VehiculoDto;
import com.car.clientead.client.exception.ApiClientException;
import com.car.clientead.repository.AlquilerRepository;
import com.car.clientead.repository.CaracteristicaVehiculoRepository;
import com.car.clientead.repository.ClienteRepository;
import com.car.clientead.repository.DetalleFacturaRepository;
import com.car.clientead.repository.VehiculoRepository;

@Service
public class DashboardService {

    private static final Locale DEFAULT_LOCALE = new Locale("es", "AR");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private CaracteristicaVehiculoRepository caracteristicaVehiculoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AlquilerRepository alquilerRepository;

    @Autowired
    private DetalleFacturaRepository detalleFacturaRepository;

    public DashboardReportView generarReporte(LocalDate desde, LocalDate hasta) {
        DashboardReportView reporte = inicializarReporte(desde, hasta);
        DateRange rango = new DateRange(reporte.getFiltroDesde(), reporte.getFiltroHasta());

        Map<String, VehiculoDto> vehiculos = vehiculoRepository.findAll().stream()
                .filter(this::vehiculoValido)
                .collect(Collectors.toMap(VehiculoDto::getId, Function.identity(), (a, b) -> a));

        if (vehiculos.isEmpty()) {
            return reporte;
        }

        Map<String, CaracteristicaVehiculoDto> caracteristicas = caracteristicaVehiculoRepository.findAll().stream()
                .filter(this::caracteristicaValida)
                .collect(Collectors.toMap(CaracteristicaVehiculoDto::getId, Function.identity(), (a, b) -> a));

        Map<String, ClienteDto> clientes = clienteRepository.findAll().stream()
                .filter(this::clienteValido)
                .collect(Collectors.toMap(ClienteDto::getId, Function.identity(), (a, b) -> a));

        Map<String, Double> montoPorAlquiler = detalleFacturaRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(det -> StringUtils.hasText(det.getAlquilerId()))
                .collect(Collectors.groupingBy(
                        DetalleFacturaDto::getAlquilerId,
                        Collectors.summingDouble(det -> det.getSubtotal() != null ? det.getSubtotal() : 0d)
                ));

        Map<String, List<AlquilerDto>> alquileresPorVehiculo = alquilerRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(alquiler -> StringUtils.hasText(alquiler.getVehiculoId()))
                .filter(alquiler -> vehiculos.containsKey(alquiler.getVehiculoId()))
                .filter(alquiler -> intervaloIntersecta(alquiler.getFechaDesde(), alquiler.getFechaHasta(), rango.desde(), rango.hasta()))
                .collect(Collectors.groupingBy(AlquilerDto::getVehiculoId));

        List<VehiculoResumenView> vehiculosReportados = alquileresPorVehiculo.entrySet().stream()
                .map(entry -> mapearVehiculo(entry.getKey(),
                        entry.getValue(),
                        vehiculos,
                        caracteristicas,
                        clientes,
                        montoPorAlquiler))
                .filter(Objects::nonNull)
                .filter(view -> !view.getAlquileres().isEmpty())
                .sorted(Comparator.comparingDouble(VehiculoResumenView::getTotalGenerado).reversed())
                .collect(Collectors.toList());

        List<ModeloRecaudacionView> recaudacion = agruparPorModelo(vehiculosReportados);
        double totalRecaudado = vehiculosReportados.stream().mapToDouble(VehiculoResumenView::getTotalGenerado).sum();
        long totalAlquileres = vehiculosReportados.stream().mapToLong(view -> view.getAlquileres().size()).sum();

        reporte.setVehiculosAlquilados(vehiculosReportados);
        reporte.setRecaudacionPorModelo(recaudacion);
        reporte.setTotalGeneralRecaudado(totalRecaudado);
        reporte.setTotalAlquileres(totalAlquileres);
        return reporte;
    }

    public byte[] generarReporteExcel(LocalDate desde, LocalDate hasta) {
        DashboardReportView reporte = generarReporte(desde, hasta);
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            crearHojaVehiculos(workbook, reporte);
            crearHojaModelos(workbook, reporte);

            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new ApiClientException("No se pudo generar el Excel del dashboard.", ex);
        }
    }

    public byte[] generarReportePdf(LocalDate desde, LocalDate hasta) {
        DashboardReportView reporte = generarReporte(desde, hasta);
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfSectionWriter writer = new PdfSectionWriter(document)) {

            writer.writeHeading("Dashboard de Alquileres");
            writer.writeParagraph(String.format("Período: %s al %s",
                    formatearFecha(reporte.getFiltroDesde()),
                    formatearFecha(reporte.getFiltroHasta())));
            writer.writeParagraph(String.format("Total recaudado: %s | Alquileres: %d",
                    formatearMonto(reporte.getTotalGeneralRecaudado()),
                    reporte.getTotalAlquileres()));
            writer.addSpacer();

            writer.writeSubHeading("Detalle por vehículo");
            if (reporte.getVehiculosAlquilados().isEmpty()) {
                writer.writeParagraph("No se registran alquileres en este período.");
            } else {
                for (VehiculoResumenView vehiculo : reporte.getVehiculosAlquilados()) {
                    writer.writeParagraph(describirVehiculo(vehiculo));
                    writer.writeParagraph(String.format("Total generado: %s (%d alquileres)",
                            formatearMonto(vehiculo.getTotalGenerado()),
                            vehiculo.getAlquileres().size()));
                    for (VehiculoAlquilerInfo alquiler : vehiculo.getAlquileres()) {
                        writer.writeBullet(String.format("%s (%s) | %s -> %s | %s",
                                alquiler.getClienteNombre(),
                                alquiler.getClienteDocumento(),
                                formatearFecha(alquiler.getFechaDesde()),
                                formatearFecha(alquiler.getFechaHasta()),
                                formatearMonto(alquiler.getMontoPagado())));
                    }
                    writer.addSpacer();
                }
            }

            writer.writeSubHeading("Recaudación por modelo");
            if (reporte.getRecaudacionPorModelo().isEmpty()) {
                writer.writeParagraph("No hubo ingresos discriminados por modelo.");
            } else {
                for (ModeloRecaudacionView modelo : reporte.getRecaudacionPorModelo()) {
                    String ticketPromedio = modelo.getCantidadAlquileres() > 0
                            ? formatearMonto(modelo.getTotalRecaudado() / modelo.getCantidadAlquileres())
                            : formatearMonto(0);
                    writer.writeBullet(String.format("%s | Total: %s | Alquileres: %d | Ticket: %s",
                            modelo.getEtiquetaModelo(),
                            formatearMonto(modelo.getTotalRecaudado()),
                            modelo.getCantidadAlquileres(),
                            ticketPromedio));
                }
            }

            document.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new ApiClientException("No se pudo generar el PDF del dashboard.", ex);
        }
    }

    public DashboardReportView inicializarReporte(LocalDate desde, LocalDate hasta) {
        DashboardReportView reporte = new DashboardReportView();
        DateRange rango = normalizarRango(desde, hasta);
        reporte.setFiltroDesde(rango.desde());
        reporte.setFiltroHasta(rango.hasta());
        reporte.setVehiculosAlquilados(Collections.emptyList());
        reporte.setRecaudacionPorModelo(Collections.emptyList());
        reporte.setTotalGeneralRecaudado(0d);
        reporte.setTotalAlquileres(0L);
        return reporte;
    }

    private VehiculoResumenView mapearVehiculo(String vehiculoId,
                                               List<AlquilerDto> alquileres,
                                               Map<String, VehiculoDto> vehiculos,
                                               Map<String, CaracteristicaVehiculoDto> caracteristicas,
                                               Map<String, ClienteDto> clientes,
                                               Map<String, Double> montoPorAlquiler) {
        VehiculoDto vehiculo = vehiculos.get(vehiculoId);
        if (vehiculo == null) {
            return null;
        }
        List<VehiculoAlquilerInfo> alquilerViews = alquileres.stream()
                .filter(Objects::nonNull)
                .map(alquiler -> mapearAlquiler(alquiler, clientes, montoPorAlquiler))
                .collect(Collectors.toList());

        VehiculoResumenView view = new VehiculoResumenView();
        view.setVehiculo(vehiculo);
        view.setCaracteristica(caracteristicas.get(vehiculo.getCaracteristicaVehiculoId()));
        view.setAlquileres(alquilerViews);
        view.setTotalGenerado(alquilerViews.stream().mapToDouble(VehiculoAlquilerInfo::getMontoPagado).sum());
        return view;
    }

    private void crearHojaVehiculos(Workbook workbook, DashboardReportView reporte) {
        Sheet sheet = workbook.createSheet("Vehículos");
        CreationHelper helper = workbook.getCreationHelper();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(helper.createDataFormat().getFormat("yyyy-mm-dd"));

        String[] headers = {"Vehículo", "Patente", "Cliente", "Documento", "Fecha desde", "Fecha hasta", "Monto"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowIndex = 1;
        for (VehiculoResumenView vehiculo : reporte.getVehiculosAlquilados()) {
            for (VehiculoAlquilerInfo alquiler : vehiculo.getAlquileres()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(describirVehiculo(vehiculo));
                row.createCell(1).setCellValue(
                        vehiculo.getVehiculo() != null ? vehiculo.getVehiculo().getPatente() : "N/D");
                row.createCell(2).setCellValue(alquiler.getClienteNombre());
                row.createCell(3).setCellValue(alquiler.getClienteDocumento());

                Cell desdeCell = row.createCell(4);
                if (alquiler.getFechaDesde() != null) {
                    desdeCell.setCellValue(java.sql.Date.valueOf(alquiler.getFechaDesde()));
                    desdeCell.setCellStyle(dateStyle);
                } else {
                    desdeCell.setCellValue("");
                }

                Cell hastaCell = row.createCell(5);
                if (alquiler.getFechaHasta() != null) {
                    hastaCell.setCellValue(java.sql.Date.valueOf(alquiler.getFechaHasta()));
                    hastaCell.setCellStyle(dateStyle);
                } else {
                    hastaCell.setCellValue("");
                }

                row.createCell(6).setCellValue(alquiler.getMontoPagado());
            }
        }

        Row resumenRow = sheet.createRow(rowIndex);
        resumenRow.createCell(0).setCellValue("Total recaudado");
        resumenRow.createCell(1).setCellValue(reporte.getTotalGeneralRecaudado());
        resumenRow.createCell(2).setCellValue("Total alquileres");
        resumenRow.createCell(3).setCellValue(reporte.getTotalAlquileres());

        autosizeColumns(sheet, headers.length);
    }

    private void crearHojaModelos(Workbook workbook, DashboardReportView reporte) {
        Sheet sheet = workbook.createSheet("Modelos");
        String[] headers = {"Modelo", "Alquileres", "Total recaudado", "Ticket promedio"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowIndex = 1;
        for (ModeloRecaudacionView modelo : reporte.getRecaudacionPorModelo()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(modelo.getEtiquetaModelo());
            row.createCell(1).setCellValue(modelo.getCantidadAlquileres());
            row.createCell(2).setCellValue(modelo.getTotalRecaudado());
            double ticketPromedio = modelo.getCantidadAlquileres() > 0
                    ? modelo.getTotalRecaudado() / modelo.getCantidadAlquileres()
                    : 0d;
            row.createCell(3).setCellValue(ticketPromedio);
        }

        Row resumenRow = sheet.createRow(rowIndex);
        resumenRow.createCell(0).setCellValue("Total recaudado");
        resumenRow.createCell(1).setCellValue(reporte.getTotalGeneralRecaudado());
        resumenRow.createCell(2).setCellValue("Total alquileres");
        resumenRow.createCell(3).setCellValue(reporte.getTotalAlquileres());

        autosizeColumns(sheet, headers.length);
    }

    private VehiculoAlquilerInfo mapearAlquiler(AlquilerDto alquiler,
                                                Map<String, ClienteDto> clientes,
                                                Map<String, Double> montoPorAlquiler) {
        VehiculoAlquilerInfo info = new VehiculoAlquilerInfo();
        info.setAlquilerId(alquiler.getId());
        info.setClienteId(alquiler.getClienteId());
        ClienteDto cliente = clientes.get(alquiler.getClienteId());
        if (cliente != null) {
            info.setClienteNombre(formatearNombre(cliente));
            info.setClienteDocumento(formatearDocumento(cliente));
        } else {
            info.setClienteNombre("Cliente no disponible");
            info.setClienteDocumento("N/D");
        }
        info.setFechaDesde(alquiler.getFechaDesde());
        info.setFechaHasta(alquiler.getFechaHasta());
        info.setMontoPagado(montoPorAlquiler.getOrDefault(alquiler.getId(), 0d));
        return info;
    }

    private List<ModeloRecaudacionView> agruparPorModelo(List<VehiculoResumenView> vehiculos) {
        Map<String, ModeloRecaudacionView> acumulado = new HashMap<>();

        for (VehiculoResumenView vehiculo : vehiculos) {
            String key = vehiculo.getCaracteristica() != null
                    ? vehiculo.getCaracteristica().getId()
                    : vehiculo.getVehiculo().getId();

            ModeloRecaudacionView resumen = acumulado.computeIfAbsent(key, k -> {
                ModeloRecaudacionView view = new ModeloRecaudacionView();
                CaracteristicaVehiculoDto caracteristica = vehiculo.getCaracteristica();
                if (caracteristica != null) {
                    view.setCaracteristicaId(caracteristica.getId());
                    view.setMarca(caracteristica.getMarca());
                    view.setModelo(caracteristica.getModelo());
                    view.setAnio(caracteristica.getAnio());
                } else {
                    view.setCaracteristicaId("SIN-CARACTERISTICA");
                    view.setMarca("Sin marca");
                    view.setModelo("Sin modelo");
                }
                view.setCantidadAlquileres(0L);
                view.setTotalRecaudado(0d);
                return view;
            });

            resumen.setCantidadAlquileres(resumen.getCantidadAlquileres() + vehiculo.getAlquileres().size());
            resumen.setTotalRecaudado(resumen.getTotalRecaudado() + vehiculo.getTotalGenerado());
        }

        return acumulado.values().stream()
                .sorted(Comparator.comparingDouble(ModeloRecaudacionView::getTotalRecaudado).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void autosizeColumns(Sheet sheet, int totalColumns) {
        for (int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private boolean intervaloIntersecta(LocalDate inicio, LocalDate fin, LocalDate desde, LocalDate hasta) {
        if (inicio == null && fin == null) {
            return true;
        }
        LocalDate realInicio = inicio != null ? inicio : fin;
        LocalDate realFin = fin != null ? fin : inicio;
        if (realInicio == null && realFin == null) {
            return true;
        }
        if (realInicio == null) {
            realInicio = realFin;
        }
        if (realFin == null) {
            realFin = realInicio;
        }
        return !(realFin.isBefore(desde) || realInicio.isAfter(hasta));
    }

    private String formatearNombre(ClienteDto cliente) {
        String nombre = StringUtils.hasText(cliente.getNombre()) ? cliente.getNombre().trim() : "";
        String apellido = StringUtils.hasText(cliente.getApellido()) ? cliente.getApellido().trim() : "";
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(nombre)) {
            sb.append(nombre);
        }
        if (StringUtils.hasText(apellido)) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(apellido);
        }
        return sb.length() > 0 ? sb.toString() : "Sin nombre";
    }

    private String formatearDocumento(ClienteDto cliente) {
        String tipo = cliente.getTipoDocumento() != null ? cliente.getTipoDocumento().name() : "";
        String numero = StringUtils.hasText(cliente.getNumeroDocumento()) ? cliente.getNumeroDocumento() : "";
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(tipo)) {
            sb.append(tipo);
        }
        if (StringUtils.hasText(numero)) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(numero);
        }
        return sb.length() > 0 ? sb.toString() : "Documento N/D";
    }

    private boolean vehiculoValido(VehiculoDto dto) {
        return dto != null && StringUtils.hasText(dto.getId()) && StringUtils.hasText(dto.getPatente());
    }

    private boolean caracteristicaValida(CaracteristicaVehiculoDto dto) {
        return dto != null && StringUtils.hasText(dto.getId());
    }

    private boolean clienteValido(ClienteDto dto) {
        return dto != null && StringUtils.hasText(dto.getId());
    }

    private String describirVehiculo(VehiculoResumenView vehiculo) {
        if (vehiculo == null) {
            return "Vehículo sin información";
        }
        StringBuilder sb = new StringBuilder();
        CaracteristicaVehiculoDto caracteristica = vehiculo.getCaracteristica();
        if (caracteristica != null) {
            if (StringUtils.hasText(caracteristica.getMarca())) {
                sb.append(caracteristica.getMarca());
            }
            if (StringUtils.hasText(caracteristica.getModelo())) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(caracteristica.getModelo());
            }
            if (caracteristica.getAnio() != null) {
                sb.append(" (").append(caracteristica.getAnio()).append(")");
            }
        }
        VehiculoDto vehiculoDto = vehiculo.getVehiculo();
        if (vehiculoDto != null && StringUtils.hasText(vehiculoDto.getPatente())) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append("Patente ").append(vehiculoDto.getPatente());
        }
        return sb.length() > 0 ? sb.toString() : "Vehículo sin información";
    }

    private String formatearFecha(LocalDate fecha) {
        return fecha != null ? DATE_FORMAT.format(fecha) : "N/D";
    }

    private String formatearMonto(double monto) {
        return String.format(DEFAULT_LOCALE, "$%,.2f", monto);
    }

    private DateRange normalizarRango(LocalDate desde, LocalDate hasta) {
        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null ? desde : fin.withDayOfMonth(1);
        if (fin.isBefore(inicio)) {
            LocalDate tmp = inicio;
            inicio = fin;
            fin = tmp;
        }
        return new DateRange(inicio, fin);
    }

    private record DateRange(LocalDate desde, LocalDate hasta) {}

    private static class PdfSectionWriter implements AutoCloseable {
        private static final float MARGIN = 50f;
        private final PDDocument document;
        private PDPageContentStream contentStream;
        private PDPage currentPage;
        private float cursorY;

        PdfSectionWriter(PDDocument document) throws IOException {
            this.document = document;
            addPage();
        }

        void writeHeading(String text) throws IOException {
            writeLine(PDType1Font.HELVETICA_BOLD, 18, text, MARGIN, 85);
            cursorY -= 6;
        }

        void writeSubHeading(String text) throws IOException {
            writeLine(PDType1Font.HELVETICA_BOLD, 14, text, MARGIN, 90);
        }

        void writeParagraph(String text) throws IOException {
            writeLine(PDType1Font.HELVETICA, 12, text, MARGIN, 95);
        }

        void writeBullet(String text) throws IOException {
            writeLine(PDType1Font.HELVETICA, 11, "\u2022 " + text, MARGIN + 10, 85);
        }

        void addSpacer() {
            cursorY -= 10;
        }

        @Override
        public void close() throws IOException {
            if (contentStream != null) {
                contentStream.close();
            }
        }

        private void writeLine(PDType1Font font, float size, String text, float offsetX, int wrapAt) throws IOException {
            List<String> lines = wrapText(text, wrapAt);
            for (String line : lines) {
                ensureSpace(size);
                contentStream.beginText();
                contentStream.setFont(font, size);
                contentStream.newLineAtOffset(offsetX, cursorY);
                contentStream.showText(line);
                contentStream.endText();
                cursorY -= size + 4;
            }
        }

        private void ensureSpace(float size) throws IOException {
            if (cursorY < MARGIN + size + 10) {
                addPage();
            }
        }

        private void addPage() throws IOException {
            if (contentStream != null) {
                contentStream.close();
            }
            currentPage = new PDPage(PDRectangle.A4);
            document.addPage(currentPage);
            contentStream = new PDPageContentStream(document, currentPage);
            cursorY = currentPage.getMediaBox().getHeight() - MARGIN;
        }

        private List<String> wrapText(String text, int maxCharacters) {
            if (!StringUtils.hasText(text)) {
                return Collections.singletonList("");
            }
            List<String> lines = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            for (String word : text.split("\\s+")) {
                int prospective = current.length() + word.length() + (current.length() > 0 ? 1 : 0);
                if (prospective > maxCharacters && current.length() > 0) {
                    lines.add(current.toString());
                    current = new StringBuilder();
                }
                if (current.length() > 0) {
                    current.append(' ');
                }
                current.append(word);
            }
            if (current.length() > 0) {
                lines.add(current.toString());
            }
            if (lines.isEmpty()) {
                lines.add("");
            }
            return lines;
        }
    }
}

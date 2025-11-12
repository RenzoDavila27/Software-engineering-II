package com.car.clientead.business.logic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.business.logic.view.ClienteAlquilerInfo;
import com.car.clientead.business.logic.view.ClienteResumenView;
import com.car.clientead.client.dto.AlquilerDto;
import com.car.clientead.client.dto.ClienteDto;
import com.car.clientead.client.dto.ContactoCorreoElectronicoDto;
import com.car.clientead.client.dto.ContactoTelefonicoDto;
import com.car.clientead.client.dto.DetalleFacturaDto;
import com.car.clientead.client.dto.DireccionDto;
import com.car.clientead.client.dto.ImagenDto;
import com.car.clientead.client.dto.NacionalidadDto;
import com.car.clientead.client.dto.VehiculoDto;
import com.car.clientead.client.exception.ApiClientException;
import com.car.clientead.repository.AlquilerRepository;
import com.car.clientead.repository.ClienteRepository;
import com.car.clientead.repository.ContactoCorreoElectronicoRepository;
import com.car.clientead.repository.ContactoTelefonicoRepository;
import com.car.clientead.repository.DetalleFacturaRepository;
import com.car.clientead.repository.DireccionRepository;
import com.car.clientead.repository.ImagenRepository;
import com.car.clientead.repository.VehiculoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AlquilerRepository alquilerRepository;

    @Autowired
    private DetalleFacturaRepository detalleFacturaRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ContactoTelefonicoRepository contactoTelefonicoRepository;

    @Autowired
    private ContactoCorreoElectronicoRepository contactoCorreoRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private ImagenRepository imagenRepository;

    @Autowired
    private NacionalidadService nacionalidadService;

    public List<ClienteResumenView> listarResumenes() {
        List<ClienteDto> clientes = clienteRepository.findAll().stream()
                .filter(this::clienteValido)
                .collect(Collectors.toList());
        if (clientes.isEmpty()) {
            return Collections.emptyList();
        }
        DatosClienteRelacionado datos = cargarDatosRelacionados(clientes);
        return clientes.stream()
                .map(cliente -> armarResumen(cliente, datos))
                .collect(Collectors.toList());
    }

    public List<ClienteDto> listarClientesBasicos() {
        return clienteRepository.findAll().stream()
                .filter(this::clienteValido)
                .collect(Collectors.toList());
    }

    public ClienteResumenView obtenerResumen(String id) {
        ClienteDto cliente = clienteRepository.findById(id);
        if (cliente == null) {
            throw new ApiClientException("No se encontró el cliente con ID: " + id);
        }
        DatosClienteRelacionado datos = cargarDatosRelacionados(Collections.singletonList(cliente));
        return armarResumen(cliente, datos);
    }

    public byte[] generarPdf(String id) {
        ClienteResumenView resumen = obtenerResumen(id);
        if (resumen == null) {
            throw new ApiClientException("No se pudo generar el PDF del cliente con ID: " + id);
        }
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(document)) {

            writer.writeRawLine(PDType1Font.HELVETICA_BOLD, 18, "Resumen del Cliente");
            writer.writeRawLine(PDType1Font.HELVETICA, 12,
                    "Nombre completo: " + resumen.getCliente().getNombre() + " " + resumen.getCliente().getApellido());
            writer.writeRawLine(PDType1Font.HELVETICA, 12,
                    "Documento: " + resumen.getCliente().getTipoDocumento() + " " + resumen.getCliente().getNumeroDocumento());
            writer.writeRawLine(PDType1Font.HELVETICA, 12,
                    "Fecha de nacimiento: " + resumen.getCliente().getFechaNacimiento());
            writer.writeRawLine(PDType1Font.HELVETICA, 12,
                    "Nacionalidad: " + (StringUtils.hasText(resumen.getNacionalidadNombre())
                            ? resumen.getNacionalidadNombre() : "No informada"));
            writer.writeRawLine(PDType1Font.HELVETICA, 12,
                    "Dirección de estadía: " + resumen.getCliente().getDireccionEstadia());
            writer.writeRawLine(PDType1Font.HELVETICA, 12,
                    "Contacto: " + (StringUtils.hasText(resumen.getContactoResumen())
                            ? resumen.getContactoResumen()
                            : "Sin datos de contacto"));
            writer.writeRawLine(PDType1Font.HELVETICA, 12,
                    "Dirección (detalle): " + (StringUtils.hasText(resumen.getDireccionResumen())
                            ? resumen.getDireccionResumen()
                            : "Sin dirección registrada"));
            writer.writeRawLine(PDType1Font.HELVETICA_BOLD, 14, "Historial de alquileres");

            if (resumen.getAlquileres().isEmpty()) {
                writer.writeRawLine(PDType1Font.HELVETICA, 12, "Sin alquileres registrados.");
            } else {
                int index = 1;
                for (ClienteAlquilerInfo info : resumen.getAlquileres()) {
                    writer.writeRawLine(PDType1Font.HELVETICA_BOLD, 12, "Alquiler #" + index++);
                    writer.writeRawLine(PDType1Font.HELVETICA, 12,
                            "Vehículo: " + (StringUtils.hasText(info.getVehiculoPatente())
                                    ? info.getVehiculoPatente()
                                    : info.getVehiculoId()));
                    writer.writeRawLine(PDType1Font.HELVETICA, 12,
                            "Período: " + info.getFechaDesde() + " al " + info.getFechaHasta());
                    writer.writeRawLine(PDType1Font.HELVETICA, 12,
                            String.format(java.util.Locale.US, "Monto pagado: $%.2f", info.getMontoPagado()));
                }
            }
            writer.writeRawLine(PDType1Font.HELVETICA_BOLD, 12,
                    String.format(java.util.Locale.US, "Total abonado por el cliente: $%.2f", resumen.getTotalPagado()));
            writer.close();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ApiClientException("No se pudo generar el PDF del cliente.", e);
        }
    }

    public String obtenerImagenDataUrl(String imagenId) {
        ImagenDto imagen = obtenerImagen(imagenId);
        if (imagen == null || imagen.getContenido() == null || imagen.getContenido().length == 0) {
            return null;
        }
        String mime = StringUtils.hasText(imagen.getMime()) ? imagen.getMime() : "image/png";
        String base64 = Base64.getEncoder().encodeToString(imagen.getContenido());
        return "data:" + mime + ";base64," + base64;
    }

    public byte[] generarExcelAlquileres(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Debe indicar la fecha desde y hasta.");
        }
        LocalDate inicio = desde;
        LocalDate fin = hasta;
        if (fin.isBefore(inicio)) {
            LocalDate tmp = inicio;
            inicio = fin;
            fin = tmp;
        }
        final LocalDate filtroDesde = inicio;
        final LocalDate filtroHasta = fin;

        List<ClienteResumenView> resumenes = listarResumenes();
        List<ClienteResumenView> filtrados = resumenes.stream()
                .map(resumen -> filtrarAlquileresPorPeriodo(resumen, filtroDesde, filtroHasta))
                .filter(res -> !res.getAlquileres().isEmpty())
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Alquileres");
            CreationHelper helper = workbook.getCreationHelper();
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(helper.createDataFormat().getFormat("yyyy-mm-dd"));

            Row header = sheet.createRow(0);
            String[] columnas = {"Cliente", "Documento", "Contacto", "Vehículo / Patente",
                    "Fecha Desde", "Fecha Hasta", "Monto", "Total Cliente"};
            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            AtomicInteger rowIndex = new AtomicInteger(1);
            filtrados.forEach(resumen -> resumen.getAlquileres().forEach(alquiler -> {
                int rowNum = rowIndex.getAndIncrement();
                Row row = sheet.createRow(rowNum);

                row.createCell(0).setCellValue(resumen.getCliente().getNombre() + " " + resumen.getCliente().getApellido());
                row.createCell(1).setCellValue(
                        resumen.getCliente().getTipoDocumento() + " " + resumen.getCliente().getNumeroDocumento());
                row.createCell(2).setCellValue(
                        StringUtils.hasText(resumen.getContactoResumen()) ? resumen.getContactoResumen() : "N/D");
                row.createCell(3).setCellValue(
                        StringUtils.hasText(alquiler.getVehiculoPatente())
                                ? alquiler.getVehiculoPatente()
                                : alquiler.getVehiculoId());

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
                row.createCell(7).setCellValue(resumen.getTotalPagado());
            }));

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new ApiClientException("No se pudo generar el archivo Excel.", ex);
        }
    }

    public ClienteDto consultar(String id) {
        return clienteRepository.findById(id);
    }

    public ClienteDto crear(ClienteDto dto) {
        validar(dto);
        return clienteRepository.create(dto);
    }

    public ClienteDto modificar(String id, ClienteDto dto) {
        validar(dto);
        return clienteRepository.update(id, dto);
    }

    public void eliminar(String id) {
        clienteRepository.delete(id);
    }

    private DatosClienteRelacionado cargarDatosRelacionados(List<ClienteDto> clientes) {
        DatosClienteRelacionado datos = new DatosClienteRelacionado();

        Set<String> clienteIds = clientes.stream()
                .map(ClienteDto::getId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        Set<String> contactoIds = clientes.stream()
                .map(ClienteDto::getContactoId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        Set<String> direccionIds = clientes.stream()
                .map(ClienteDto::getDireccionId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        List<AlquilerDto> alquileres = safeListFetch(alquilerRepository::findAll, "alquileres");
        datos.alquileresPorCliente = alquileres.stream()
                .filter(Objects::nonNull)
                .filter(a -> clienteIds.contains(a.getClienteId()))
                .collect(Collectors.groupingBy(AlquilerDto::getClienteId));

        List<VehiculoDto> vehiculos = safeListFetch(vehiculoRepository::findAll, "vehículos");
        datos.vehiculosPorId = vehiculos.stream()
                .filter(Objects::nonNull)
                .filter(v -> StringUtils.hasText(v.getId()))
                .collect(Collectors.toMap(VehiculoDto::getId, Function.identity(), (a, b) -> a));

        List<DetalleFacturaDto> detalles = safeListFetch(detalleFacturaRepository::findAll, "detalles de factura");
        datos.montoPorAlquiler = detalles.stream()
                .filter(Objects::nonNull)
                .filter(det -> StringUtils.hasText(det.getAlquilerId()))
                .collect(Collectors.groupingBy(
                        DetalleFacturaDto::getAlquilerId,
                        Collectors.summingDouble(det -> det.getSubtotal() != null ? det.getSubtotal() : 0d)
                ));

        List<NacionalidadDto> nacionalidades = safeListFetch(nacionalidadService::listar, "nacionalidades");
        datos.nacionalidades = nacionalidades.stream()
                .filter(Objects::nonNull)
                .filter(n -> StringUtils.hasText(n.getId()))
                .collect(Collectors.toMap(NacionalidadDto::getId, NacionalidadDto::getNombre, (a, b) -> a));

        Map<String, ContactoTelefonicoDto> telefonos = new HashMap<>();
        Map<String, ContactoCorreoElectronicoDto> correos = new HashMap<>();
        for (String contactoId : contactoIds) {
            ContactoTelefonicoDto telefono = obtenerContactoTelefonico(contactoId);
            if (telefono != null) {
                telefonos.put(contactoId, telefono);
                continue;
            }
            ContactoCorreoElectronicoDto correo = obtenerContactoCorreo(contactoId);
            if (correo != null) {
                correos.put(contactoId, correo);
            }
        }
        datos.contactoTelefonicoPorId = telefonos;
        datos.contactoCorreoPorId = correos;

        datos.direccionPorId = direccionIds.isEmpty()
                ? Collections.emptyMap()
                : direccionIds.stream()
                        .map(this::obtenerDireccion)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(DireccionDto::getId, Function.identity(), (a, b) -> a));
        return datos;
    }

    private <T> List<T> safeListFetch(Supplier<List<T>> supplier, String descripcion) {
        try {
            List<T> result = supplier.get();
            return result != null ? result : Collections.emptyList();
        } catch (ApiClientException ex) {
            log.warn("No se pudo obtener {}: {}. Se continuará con datos vacíos.", descripcion, ex.getMessage());
            return Collections.emptyList();
        }
    }

    private ClienteResumenView armarResumen(ClienteDto cliente, DatosClienteRelacionado datos) {
        ClienteResumenView view = new ClienteResumenView();
        view.setCliente(cliente);
        view.setNacionalidadNombre(datos.nacionalidades.get(cliente.getNacionalidadId()));

        List<ClienteAlquilerInfo> alquileres = datos.alquileresPorCliente.getOrDefault(cliente.getId(), Collections.emptyList())
                .stream()
                .map(alquiler -> mapearAlquiler(alquiler, datos))
                .collect(Collectors.toList());
        view.setAlquileres(alquileres);
        double total = alquileres.stream().mapToDouble(ClienteAlquilerInfo::getMontoPagado).sum();
        view.setTotalPagado(total);

        ContactoTelefonicoDto contactoTel = datos.contactoTelefonicoPorId.get(cliente.getContactoId());
        ContactoCorreoElectronicoDto contactoMail = datos.contactoCorreoPorId.get(cliente.getContactoId());
        if (contactoTel != null) {
            view.setTelefonoPrincipal(contactoTel.getTelefono());
            view.setContactoResumen(construirDescripcionTelefono(contactoTel));
            String normalizado = normalizarTelefono(view.getTelefonoPrincipal());
            if (StringUtils.hasText(normalizado)) {
                view.setWhatsappUrl("https://wa.me/" + normalizado);
            }
        } else if (contactoMail != null) {
            view.setContactoResumen(construirDescripcionCorreo(contactoMail));
        }

        DireccionDto direccion = datos.direccionPorId.get(cliente.getDireccionId());
        view.setDireccionResumen(formatearDireccion(direccion));
        return view;
    }

    private ClienteAlquilerInfo mapearAlquiler(AlquilerDto alquiler, DatosClienteRelacionado datos) {
        ClienteAlquilerInfo info = new ClienteAlquilerInfo();
        info.setAlquilerId(alquiler.getId());
        info.setVehiculoId(alquiler.getVehiculoId());
        info.setFechaDesde(alquiler.getFechaDesde());
        info.setFechaHasta(alquiler.getFechaHasta());
        info.setMontoPagado(datos.montoPorAlquiler.getOrDefault(alquiler.getId(), 0d));

        VehiculoDto vehiculo = datos.vehiculosPorId.get(alquiler.getVehiculoId());
        if (vehiculo != null) {
            info.setVehiculoPatente(vehiculo.getPatente());
        }
        return info;
    }

    private ClienteResumenView filtrarAlquileresPorPeriodo(ClienteResumenView resumen,
                                                           LocalDate desde,
                                                           LocalDate hasta) {
        List<ClienteAlquilerInfo> filtrados = resumen.getAlquileres().stream()
                .filter(alquiler -> intervaloIntersecta(alquiler.getFechaDesde(), alquiler.getFechaHasta(), desde, hasta))
                .collect(Collectors.toList());

        ClienteResumenView copia = new ClienteResumenView();
        copia.setCliente(resumen.getCliente());
        copia.setNacionalidadNombre(resumen.getNacionalidadNombre());
        copia.setContactoResumen(resumen.getContactoResumen());
        copia.setDireccionResumen(resumen.getDireccionResumen());
        copia.setTelefonoPrincipal(resumen.getTelefonoPrincipal());
        copia.setWhatsappUrl(resumen.getWhatsappUrl());
        copia.setAlquileres(filtrados);
        copia.setTotalPagado(filtrados.stream().mapToDouble(ClienteAlquilerInfo::getMontoPagado).sum());
        return copia;
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

    private String normalizarTelefono(String telefono) {
        if (!StringUtils.hasText(telefono)) {
            return null;
        }
        String digits = telefono.replaceAll("[^0-9+]", "");
        digits = digits.replace("+", "");
        return StringUtils.hasText(digits) ? digits : null;
    }

    private static List<String> wrapText(String text, int maxCharacters) {
        if (!StringUtils.hasText(text)) {
            return Collections.singletonList("");
        }
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : text.split("\\s+")) {
            int prospectiveLength = current.length() + word.length() + (current.length() > 0 ? 1 : 0);
            if (prospectiveLength > maxCharacters && current.length() > 0) {
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

    private String construirDescripcionTelefono(ContactoTelefonicoDto contacto) {
        if (contacto == null || !StringUtils.hasText(contacto.getTelefono())) {
            return null;
        }
        StringBuilder sb = new StringBuilder(contacto.getTelefono());
        if (StringUtils.hasText(contacto.getTipoTelefono())) {
            sb.append(" (").append(contacto.getTipoTelefono()).append(')');
        }
        return sb.toString();
    }

    private String construirDescripcionCorreo(ContactoCorreoElectronicoDto contacto) {
        if (contacto == null || !StringUtils.hasText(contacto.getEmail())) {
            return null;
        }
        StringBuilder sb = new StringBuilder(contacto.getEmail());
        if (StringUtils.hasText(contacto.getObservacion())) {
            sb.append(" (").append(contacto.getObservacion()).append(')');
        }
        return sb.toString();
    }

    private String formatearDireccion(DireccionDto direccion) {
        if (direccion == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(direccion.getCalle())) {
            sb.append(direccion.getCalle());
        }
        if (StringUtils.hasText(direccion.getNumeracion())) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(direccion.getNumeracion());
        }
        if (StringUtils.hasText(direccion.getBarrio())) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append("Barrio ").append(direccion.getBarrio());
        }
        if (StringUtils.hasText(direccion.getManzanaPiso())) {
            sb.append(" Mza/Piso ").append(direccion.getManzanaPiso());
        }
        if (StringUtils.hasText(direccion.getCasaDepartamento())) {
            sb.append(" Dept ").append(direccion.getCasaDepartamento());
        }
        if (StringUtils.hasText(direccion.getReferencia())) {
            sb.append(" (").append(direccion.getReferencia()).append(")");
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private ContactoTelefonicoDto obtenerContactoTelefonico(String contactoId) {
        if (!StringUtils.hasText(contactoId)) {
            return null;
        }
        try {
            return contactoTelefonicoRepository.findById(contactoId);
        } catch (ApiClientException ex) {
            return null;
        }
    }

    private DireccionDto obtenerDireccion(String direccionId) {
        if (!StringUtils.hasText(direccionId)) {
            return null;
        }
        try {
            return direccionRepository.findById(direccionId);
        } catch (ApiClientException ex) {
            return null;
        }
    }

    private ContactoCorreoElectronicoDto obtenerContactoCorreo(String contactoId) {
        if (!StringUtils.hasText(contactoId)) {
            return null;
        }
        try {
            return contactoCorreoRepository.findById(contactoId);
        } catch (ApiClientException ex) {
            return null;
        }
    }

    private ImagenDto obtenerImagen(String imagenId) {
        if (!StringUtils.hasText(imagenId)) {
            return null;
        }
        try {
            return imagenRepository.findById(imagenId);
        } catch (ApiClientException ex) {
            return null;
        }
    }

    private boolean clienteValido(ClienteDto dto) {
        return dto != null
                && StringUtils.hasText(dto.getNombre())
                && StringUtils.hasText(dto.getApellido())
                && dto.getFechaNacimiento() != null;
    }

    private void validar(ClienteDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos del cliente no pueden ser nulos.");
        }
        if (!StringUtils.hasText(dto.getNombre()) || !StringUtils.hasText(dto.getApellido())) {
            throw new IllegalArgumentException("El nombre y apellido del cliente son obligatorios.");
        }
        if (dto.getFechaNacimiento() == null || dto.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria y debe ser válida.");
        }
        if (dto.getTipoDocumento() == null) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getNumeroDocumento())) {
            throw new IllegalArgumentException("El número de documento es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getDireccionEstadia())) {
            throw new IllegalArgumentException("La dirección de estadía es obligatoria.");
        }
        if (!StringUtils.hasText(dto.getNacionalidadId())) {
            throw new IllegalArgumentException("Debe indicar la nacionalidad del cliente.");
        }
        if (!StringUtils.hasText(dto.getContactoId())
                || !StringUtils.hasText(dto.getDireccionId())
                || !StringUtils.hasText(dto.getImagenId())) {
            throw new IllegalArgumentException("Contacto, dirección e imagen son obligatorios.");
        }
    }

    private static class DatosClienteRelacionado {
        private Map<String, List<AlquilerDto>> alquileresPorCliente = Collections.emptyMap();
        private Map<String, VehiculoDto> vehiculosPorId = Collections.emptyMap();
        private Map<String, Double> montoPorAlquiler = Collections.emptyMap();
        private Map<String, String> nacionalidades = Collections.emptyMap();
        private Map<String, ContactoTelefonicoDto> contactoTelefonicoPorId = Collections.emptyMap();
        private Map<String, ContactoCorreoElectronicoDto> contactoCorreoPorId = Collections.emptyMap();
        private Map<String, DireccionDto> direccionPorId = Collections.emptyMap();
    }

    private static class PdfWriter implements AutoCloseable {
        private final PDDocument document;
        private PDPageContentStream contentStream;
        private PDPage currentPage;
        private float cursorY;
        private final float margin = 50f;

        PdfWriter(PDDocument document) throws IOException {
            this.document = document;
            addPage();
        }

        void writeRawLine(PDFont font, float size, String text) throws IOException {
            List<String> lines = wrapText(text, 95);
            for (String line : lines) {
                ensureSpace(size);
                contentStream.beginText();
                contentStream.setFont(font, size);
                contentStream.newLineAtOffset(margin, cursorY);
                contentStream.showText(line);
                contentStream.endText();
                cursorY -= size + 4;
            }
        }

        private void ensureSpace(float size) throws IOException {
            if (cursorY < margin + size + 10) {
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
            cursorY = currentPage.getMediaBox().getHeight() - margin;
        }

        @Override
        public void close() throws IOException {
            if (contentStream != null) {
                contentStream.close();
            }
        }
    }
}

package com.car.business.logic.service;

import com.car.business.domain.Alquiler;
import com.car.business.domain.Cliente;
import com.car.business.domain.CostoVehiculo;
import com.car.business.domain.DetalleFactura;
import com.car.business.domain.Documentacion;
import com.car.business.domain.Factura;
import com.car.business.domain.Usuario;
import com.car.business.domain.Vehiculo;
import com.car.business.domain.enums.EstadoFactura;
import com.car.business.domain.enums.TipoDocumentacion;
import com.car.business.logic.error.BusinessException;
import com.car.controller.rest.api.dto.DocumentoAdjuntoDto;
import com.car.controller.rest.api.dto.MercadoPagoPreferenceRequest;
import com.car.controller.rest.api.dto.MercadoPagoPreferenceResponse;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPSearchRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class MercadoPagoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MercadoPagoService.class);
    private static final String REFERENCE_PREFIX = "ALQUILER";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String MP_DOCUMENT_PATH = "/home/renzo";

    private final String accessToken;
    private final VehiculoService vehiculoService;
    private final UsuarioService usuarioService;
    private final CostoVehiculoService costoVehiculoService;
    private final ClienteService clienteService;
    private final DocumentacionService documentacionService;
    private final AlquilerService alquilerService;
    private final FacturaService facturaService;

    public MercadoPagoService(@Value("${mercadopago.access-token:}") String accessToken,
        VehiculoService vehiculoService, UsuarioService usuarioService, CostoVehiculoService costoVehiculoService,
        ClienteService clienteService, DocumentacionService documentacionService,
        AlquilerService alquilerService, FacturaService facturaService) {
        this.accessToken = accessToken;
        this.vehiculoService = vehiculoService;
        this.usuarioService = usuarioService;
        this.costoVehiculoService = costoVehiculoService;
        this.clienteService = clienteService;
        this.documentacionService = documentacionService;
        this.alquilerService = alquilerService;
        this.facturaService = facturaService;
    }

    @PostConstruct
    void configureSdk() {
        if (accessToken != null && !accessToken.isBlank()) {
            MercadoPagoConfig.setAccessToken(accessToken.trim());
        }
    }

    public MercadoPagoPreferenceResponse createPreference(MercadoPagoPreferenceRequest request) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new BusinessException("Configura el token de acceso de Mercado Pago antes de crear preferencias");
        }

        Cliente cliente = obtenerClienteAutenticado();
        Vehiculo vehiculo = obtenerVehiculo(request.vehiculoId());
        validarFechas(request.fechaDesde(), request.fechaHasta());

        long dias = calcularDias(request.fechaDesde(), request.fechaHasta());
        BigDecimal monto = calcularMonto(vehiculo, dias);
        DocumentosGuardados documentosGuardados = guardarDocumentosTemporales(
            request.docDni(), request.docLicencia(), cliente.getId());

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
            .title(request.title())
            .description(request.description())
            .quantity(1)
            .currencyId(resolveCurrency(request.currencyId()))
            .unitPrice(monto)
            .build();

        String externalReference = buildExternalReference(cliente.getId(), vehiculo.getId(),
            request.fechaDesde(), request.fechaHasta(), dias, monto,
            documentosGuardados.dniPath().toString(), documentosGuardados.licenciaPath().toString());

        PreferenceRequest.PreferenceRequestBuilder preferenceBuilder = PreferenceRequest.builder()
            .items(List.of(itemRequest))
            .externalReference(externalReference);

        if (hasBackUrls(request) || (request.autoReturn() != null && !request.autoReturn().isBlank())) {
            String success = normalizeUrl(request.successUrl());
            String failure = normalizeUrl(request.failureUrl());
            String pending = normalizeUrl(request.pendingUrl());

            PreferenceBackUrlsRequest.PreferenceBackUrlsRequestBuilder backUrlsBuilder = PreferenceBackUrlsRequest.builder();
            if (success != null) {
                backUrlsBuilder.success(success);
            }
            if (failure != null) {
                backUrlsBuilder.failure(failure);
            }
            if (pending != null) {
                backUrlsBuilder.pending(pending);
            }

            PreferenceBackUrlsRequest backUrls = backUrlsBuilder.build();
            preferenceBuilder.backUrls(backUrls);
            if (request.autoReturn() != null && !request.autoReturn().isBlank()) {
                preferenceBuilder.autoReturn(request.autoReturn());
            }
        }

        if (request.notificationUrl() != null && !request.notificationUrl().isBlank()) {
            preferenceBuilder.notificationUrl(request.notificationUrl().trim());
        }

        PreferenceRequest preferenceRequest = preferenceBuilder.build();

        PreferenceClient client = new PreferenceClient();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Preferencia a enviar: title={}, amount={}, externalRef={}",
                request.title(), monto, externalReference);
        }
        try {
            Preference preference = client.create(preferenceRequest);
            // No se crea el alquiler aún, por lo que devolvemos null en el último campo
            return new MercadoPagoPreferenceResponse(
                preference.getId(), preference.getInitPoint(), preference.getSandboxInitPoint());
        } catch (MPApiException ex) {
            LOGGER.error("Error al crear preferencia en Mercado Pago (API)", ex);
            String detalle = ex.getApiResponse() != null ? ex.getApiResponse().getContent() : ex.getMessage();
            throw new BusinessException("Mercado Pago rechazó la creación de la preferencia: "
                + (detalle != null ? detalle : "Respuesta desconocida"));
        } catch (MPException ex) {
            LOGGER.error("Error al crear preferencia en Mercado Pago", ex);
            throw new BusinessException("No fue posible comunicarse con Mercado Pago: "
                + (ex.getMessage() != null ? ex.getMessage() : "Error desconocido"));
        }
    }

    public Optional<PreferenceMetadata> processPaymentNotification(String paymentId, String preferenceId) {
        Payment payment = fetchPayment(paymentId, preferenceId);
        if (payment == null) {
            LOGGER.warn("No se encontró información del pago para paymentId={} preferenceId={}", paymentId, preferenceId);
            return Optional.empty();
        }

        if (payment.getStatus() == null || !"approved".equalsIgnoreCase(payment.getStatus())) {
            LOGGER.info("Pago {} con estado {}. Se esperará la confirmación de Mercado Pago.",
                payment.getId(), payment.getStatus());
            return Optional.empty();
        }

        String externalReference = payment.getExternalReference();
        if (externalReference == null || externalReference.isBlank()) {
            throw new BusinessException("Mercado Pago no informó la referencia externa del pago "
                + payment.getId());
        }

        PreferenceMetadata metadata = parseExternalReference(externalReference);
        LOGGER.info("Pago confirmado para cliente {} – vehículo {} – período {} a {} – monto {} (paymentId={})",
            metadata.clienteId(), metadata.vehiculoId(), metadata.fechaDesde(), metadata.fechaHasta(),
            metadata.monto(), payment.getId());
        registrarAlquilerYFactura(metadata, payment);
        return Optional.of(metadata);
    }

    private Cliente obtenerClienteAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BusinessException("No se pudo determinar el usuario autenticado");
        }
        String username = extraerNombreUsuario(authentication);
        Usuario usuario = usuarioService.obtenerPorNombreUsuario(username);
        if (!(usuario.getPersona() instanceof Cliente cliente)) {
            throw new BusinessException("El usuario autenticado no está asociado a un cliente");
        }
        return cliente;
    }

    private String extraerNombreUsuario(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        String name = authentication.getName();
        if (name == null || name.isBlank()) {
            throw new BusinessException("No se pudo determinar el nombre del usuario autenticado");
        }
        return name;
    }

    private Vehiculo obtenerVehiculo(String vehiculoId) {
        if (vehiculoId == null || vehiculoId.isBlank()) {
            throw new BusinessException("El identificador del vehículo es obligatorio");
        }
        return vehiculoService.obtener(vehiculoId)
            .orElseThrow(() -> new BusinessException("No se encontró el vehículo indicado"));
    }

    private void validarFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new BusinessException("Las fechas del alquiler son obligatorias");
        }
        if (hasta.isBefore(desde)) {
            throw new BusinessException("La fecha hasta no puede ser anterior a la fecha desde");
        }
    }

    private long calcularDias(LocalDate desde, LocalDate hasta) {
        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;
        if (dias <= 0) {
            throw new BusinessException("La duración del alquiler es inválida");
        }
        return dias;
    }

    private BigDecimal calcularMonto(Vehiculo vehiculo, long dias) {
        if (vehiculo.getCaracteristicaVehiculo() == null
            || vehiculo.getCaracteristicaVehiculo().getId() == null) {
            throw new BusinessException("El vehículo no posee características para calcular el costo");
        }
        CostoVehiculo costoVigente =
            costoVehiculoService.obtenerCostoVigente(vehiculo.getCaracteristicaVehiculo().getId());
        double costoDiario = costoVigente.getCosto();
        if (costoDiario <= 0) {
            throw new BusinessException("El costo diario del vehículo debe ser mayor a cero");
        }
        BigDecimal total = BigDecimal.valueOf(costoDiario).multiply(BigDecimal.valueOf(dias));
        return sanitizeAmount(total);
    }

    private String resolveCurrency(String currencyId) {
        if (currencyId == null || currencyId.isBlank()) {
            return "ARS";
        }
        return currencyId.trim().toUpperCase(Locale.ROOT);
    }

    private BigDecimal sanitizeAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean hasBackUrls(MercadoPagoPreferenceRequest request) {
        return (request.successUrl() != null && !request.successUrl().isBlank())
            || (request.failureUrl() != null && !request.failureUrl().isBlank())
            || (request.pendingUrl() != null && !request.pendingUrl().isBlank());
    }

    private String normalizeUrl(String url) {
        if (url == null) {
            return null;
        }
        String trimmed = url.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }

    private String buildExternalReference(String clienteId, String vehiculoId, LocalDate desde, LocalDate hasta,
        long dias, BigDecimal monto, String dniPath, String licenciaPath) {
        if (clienteId == null || clienteId.isBlank()) {
            throw new BusinessException("No se pudo determinar el cliente del pago");
        }
        if (dniPath == null || dniPath.isBlank() || licenciaPath == null || licenciaPath.isBlank()) {
            throw new BusinessException("No se pudieron almacenar los documentos aportados por el cliente");
        }
        return String.join("|",
            REFERENCE_PREFIX,
            clienteId,
            vehiculoId,
            DATE_FORMATTER.format(desde),
            DATE_FORMATTER.format(hasta),
            String.valueOf(dias),
            monto.toPlainString(),
            dniPath,
            licenciaPath
        );
    }

    private PreferenceMetadata parseExternalReference(String externalReference) {
        if (externalReference == null || externalReference.isBlank()) {
            throw new BusinessException("Mercado Pago no devolvió la referencia externa");
        }

        String trimmed = externalReference.trim();
        String[] parts = trimmed.split("\\|");
        if (parts.length < 9 || !REFERENCE_PREFIX.equalsIgnoreCase(parts[0])) {
            throw new BusinessException("La referencia externa no contiene la información esperada");
        }
        String clienteId = parts[1];
        String vehiculoId = parts[2];
        LocalDate desde = LocalDate.parse(parts[3], DATE_FORMATTER);
        LocalDate hasta = LocalDate.parse(parts[4], DATE_FORMATTER);
        long dias = Long.parseLong(parts[5]);
        BigDecimal monto = sanitizeAmount(new BigDecimal(parts[6]));
        String dniPath = parts[7];
        String licenciaPath = parts[8];
        return new PreferenceMetadata(clienteId, vehiculoId, desde, hasta, dias, monto, dniPath, licenciaPath);
    }

    private Payment fetchPayment(String paymentId, String preferenceId) {
        if (paymentId != null && !paymentId.isBlank()) {
            try {
                PaymentClient client = new PaymentClient();
                return client.get(Long.parseLong(paymentId));
            } catch (NumberFormatException ex) {
                throw new BusinessException("Identificador de pago inválido: " + paymentId);
            } catch (MPApiException | MPException ex) {
                LOGGER.error("Error consultando el pago {} en Mercado Pago", paymentId, ex);
                throw new BusinessException("No fue posible consultar el pago en Mercado Pago");
            }
        }

        if (preferenceId != null && !preferenceId.isBlank()) {
            try {
                PaymentClient client = new PaymentClient();
                MPSearchRequest searchRequest = MPSearchRequest.builder()
                    .limit(1)
                    .offset(0)
                    .filters(Map.of("preference_id", preferenceId))
                    .build();
                return client.search(searchRequest, null).getResults().stream().findFirst().orElse(null);
            } catch (MPApiException | MPException ex) {
                LOGGER.error("Error consultando la preferencia {} en Mercado Pago", preferenceId, ex);
                throw new BusinessException("No fue posible consultar la preferencia en Mercado Pago");
            }
        }

        return null;
    }

    public record PreferenceMetadata(
        String clienteId,
        String vehiculoId,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        long dias,
        BigDecimal monto,
        String dniPath,
        String licenciaPath) {
    }

    private void registrarAlquilerYFactura(PreferenceMetadata metadata, Payment payment) {
        Cliente cliente = clienteService.obtener(metadata.clienteId())
            .orElseThrow(() -> new BusinessException("No se encontró el cliente asociado al pago"));
        Vehiculo vehiculo = obtenerVehiculo(metadata.vehiculoId());
        Documentacion documentacion = registrarDocumentacionDesdePaths(metadata);
        Alquiler alquiler = registrarAlquiler(metadata, cliente, vehiculo, documentacion);
        registrarFactura(metadata, alquiler);
        LOGGER.info("Alquiler {} y factura registrados para el pago {}", alquiler.getId(), payment != null ? payment.getId() : "sin-id");
    }

    private Alquiler registrarAlquiler(PreferenceMetadata metadata, Cliente cliente, Vehiculo vehiculo, Documentacion documentacion) {
        Alquiler alquiler = new Alquiler();
        alquiler.setCliente(cliente);
        alquiler.setVehiculo(vehiculo);
        alquiler.setDocumentacion(documentacion);
        alquiler.setFechaDesde(metadata.fechaDesde());
        alquiler.setFechaHasta(metadata.fechaHasta());
        return alquilerService.alta(alquiler);
    }

    private void registrarFactura(PreferenceMetadata metadata, Alquiler alquiler) {
        DetalleFactura detalle = new DetalleFactura();
        long dias = Math.max(1L, metadata.dias());
        int cantidad = dias > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) dias;
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(metadata.monto().doubleValue());
        detalle.setAlquiler(alquiler);

        Factura factura = new Factura();
        factura.setNumeroFactura(generarNumeroFactura());
        factura.setFechaFactura(LocalDate.now());
        factura.setEstado(EstadoFactura.PAGADA);
        factura.setTotalPagado(metadata.monto().doubleValue());
        detalle.setFactura(factura);
        factura.setDetalles(new ArrayList<>(List.of(detalle)));
        facturaService.alta(factura);
    }

    private DocumentosGuardados guardarDocumentosTemporales(DocumentoAdjuntoDto docDni,
        DocumentoAdjuntoDto docLicencia, String clienteId) {
        if (docDni == null || docLicencia == null) {
            throw new BusinessException("Los documentos del cliente son obligatorios para completar la reserva");
        }
        try {
            String clienteFolder = sanitizeFileName(clienteId);
            String carpetaAlquiler = "alquiler-" + UUID.randomUUID();
            Path targetDir = Paths.get(MP_DOCUMENT_PATH, clienteFolder, carpetaAlquiler);
            Files.createDirectories(targetDir);

            Path dniPath = guardarArchivoDocumento(docDni, targetDir, "dni");
            Path licenciaPath = guardarArchivoDocumento(docLicencia, targetDir, "licencia");

            return new DocumentosGuardados(targetDir, dniPath, licenciaPath);
        } catch (IOException | IllegalArgumentException ex) {
            LOGGER.error("Error almacenando la documentación del cliente {}", clienteId, ex);
            throw new BusinessException("No fue posible almacenar la documentación del cliente. Intentalo nuevamente.");
        }
    }

    private Documentacion registrarDocumentacionDesdePaths(PreferenceMetadata metadata) {
        Path dniPath = Paths.get(metadata.dniPath());
        Path licenciaPath = Paths.get(metadata.licenciaPath());
        Path folder = dniPath.getParent();
        if (folder == null || !Files.exists(dniPath) || !Files.exists(licenciaPath)) {
            throw new BusinessException("No se encontraron los archivos de documentación almacenados anteriormente.");
        }
        Documentacion documentacion = new Documentacion();
        documentacion.setTipoDocumentacion(TipoDocumentacion.DOCUMENTO_IDENTIDAD);
        documentacion.setNombreArchivo(folder.getFileName().toString());
        documentacion.setPathArchivo(folder.toAbsolutePath().toString());
        documentacion.setObservacion("Incluye DNI (" + dniPath.getFileName() + ") y Licencia (" + licenciaPath.getFileName() + ")");
        return documentacionService.alta(documentacion);
    }

    private record DocumentosGuardados(Path folder, Path dniPath, Path licenciaPath) {}

    private Path guardarArchivoDocumento(DocumentoAdjuntoDto documento, Path targetDir, String prefix) throws IOException {
        byte[] contenido = Base64.getDecoder().decode(documento.contenidoBase64());
        String original = documento.nombreArchivo() != null ? documento.nombreArchivo() : UUID.randomUUID().toString();
        String nombreLimpio = sanitizeFileName(prefix + "-" + original);
        Path destino = targetDir.resolve(nombreLimpio);
        Files.write(destino, contenido);
        return destino;
    }

    private String sanitizeFileName(String original) {
        if (original == null || original.isBlank()) {
            return "archivo-" + UUID.randomUUID();
        }
        return original.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private long generarNumeroFactura() {
        return System.currentTimeMillis();
    }
}

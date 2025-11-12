package com.car.business.logic.service;

import com.car.business.domain.Alquiler;
import com.car.business.domain.Cliente;
import com.car.business.domain.CostoVehiculo;
import com.car.business.domain.DetalleFactura;
import com.car.business.domain.Factura;
import com.car.business.domain.Usuario;
import com.car.business.domain.Vehiculo;
import com.car.business.domain.enums.EstadoFactura;
import com.car.business.domain.enums.TipoPago;
import com.car.business.logic.error.BusinessException;
import com.car.business.logic.service.CostoVehiculoService;
import com.car.business.logic.service.UsuarioService;
import com.car.controller.rest.api.dto.MercadoPagoPreferenceRequest;
import com.car.controller.rest.api.dto.MercadoPagoPreferenceResponse;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.net.MPSearchRequest;
import com.mercadopago.resources.payment.Payment;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MercadoPagoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MercadoPagoService.class);

    private static final String ALQUILER_REFERENCE_PREFIX = "ALQUILER|";

    private final String accessToken;
    private final FacturaService facturaService;
    private final AlquilerService alquilerService;
    private final VehiculoService vehiculoService;
    private final UsuarioService usuarioService;
    private final CostoVehiculoService costoVehiculoService;

    public MercadoPagoService(@Value("${mercadopago.access-token:}") String accessToken,
        FacturaService facturaService, AlquilerService alquilerService, VehiculoService vehiculoService,
        UsuarioService usuarioService, CostoVehiculoService costoVehiculoService) {
        this.accessToken = accessToken;
        this.facturaService = facturaService;
        this.alquilerService = alquilerService;
        this.vehiculoService = vehiculoService;
        this.usuarioService = usuarioService;
        this.costoVehiculoService = costoVehiculoService;
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

        validarFormaDePago(request.formaDePago());

        Alquiler alquiler = crearAlquiler(request);
        Vehiculo vehiculo = alquiler.getVehiculo();
        long diasDeAlquiler = calcularDiasDeAlquiler(alquiler);
        double costoDiario = resolverCostoDiario(vehiculo);
        double total = calcularTotalAlquiler(costoDiario, diasDeAlquiler);

        String currency = resolveCurrency(request.currencyId());
        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
            .title(request.title())
            .description(request.description())
            .quantity(1)
            .currencyId(currency)
            .unitPrice(sanitizeAmount(BigDecimal.valueOf(total)))
            .build();

        PreferenceRequest.PreferenceRequestBuilder preferenceBuilder = PreferenceRequest.builder()
            .items(List.of(itemRequest))
            .externalReference(buildExternalReference(alquiler.getId()));

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
            LOGGER.debug("Configurando back URLs de Mercado Pago: success={}, failure={}, pending={}", success, failure,
                pending);
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
            PreferenceBackUrlsRequest backUrls = preferenceRequest.getBackUrls();
            LOGGER.debug("Preferencia a enviar: title={}, amount={}, externalRef={}, backUrls={}",
                request.title(), total, buildExternalReference(alquiler.getId()), backUrls);
        }
        try {
            Preference preference = client.create(preferenceRequest);
            return new MercadoPagoPreferenceResponse(preference.getId(), preference.getInitPoint(),
                preference.getSandboxInitPoint(), alquiler.getId());
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

    private void validarFormaDePago(TipoPago formaDePago) {
        if (formaDePago == null) {
            throw new BusinessException("La forma de pago es obligatoria");
        }
        if (formaDePago != TipoPago.BILLETERA_VIRTUAL) {
            throw new BusinessException("Solo se admite el pago mediante Mercado Pago");
        }
    }

    private Alquiler crearAlquiler(MercadoPagoPreferenceRequest request) {
        Cliente cliente = obtenerClienteAutenticado();
        Vehiculo vehiculo = obtenerVehiculo(request.vehiculoId());

        Alquiler alquiler = new Alquiler();
        alquiler.setCliente(cliente);
        alquiler.setVehiculo(vehiculo);
        alquiler.setFechaDesde(request.fechaDesde());
        alquiler.setFechaHasta(request.fechaHasta());

        return alquilerService.alta(alquiler);
    }

    private Vehiculo obtenerVehiculo(String vehiculoId) {
        if (vehiculoId == null || vehiculoId.isBlank()) {
            throw new BusinessException("El identificador del vehículo es obligatorio");
        }
        return vehiculoService.obtener(vehiculoId)
            .orElseThrow(() -> new BusinessException("No se encontró el vehículo indicado"));
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

    private String buildExternalReference(String alquilerId) {
        if (alquilerId == null || alquilerId.isBlank()) {
            throw new BusinessException("No se pudo construir la referencia externa del alquiler");
        }
        return ALQUILER_REFERENCE_PREFIX + alquilerId;
    }

    @Transactional
    public Factura processSuccessfulPayment(String paymentId, String externalReference) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new BusinessException("Mercado Pago no devolvió un identificador de pago válido");
        }

        String alquilerId = extractAlquilerId(externalReference);
        Alquiler alquiler = alquilerService.obtener(alquilerId)
            .orElseThrow(() -> new BusinessException("No se encontró el alquiler asociado al pago"));

        Vehiculo vehiculo = obtenerVehiculoDesdeServicio(alquiler);
        long diasDeAlquiler = calcularDiasDeAlquiler(alquiler);
        double costoDiario = resolverCostoDiario(vehiculo);
        double total = calcularTotalAlquiler(costoDiario, diasDeAlquiler);

        Long numeroFactura = resolveInvoiceNumber(paymentId);

        return crearFacturaParaAlquiler(numeroFactura, diasDeAlquiler, total, alquiler);
    }

    private Long resolveInvoiceNumber(String paymentId) {
        try {
            return Long.parseLong(paymentId);
        } catch (NumberFormatException ex) {
            LOGGER.warn("El payment_id '{}' no es numérico. Se generará un número alternativo.", paymentId);
            return System.currentTimeMillis();
        }
    }

    private String extractAlquilerId(String externalReference) {
        if (externalReference == null || externalReference.isBlank()) {
            throw new BusinessException("Mercado Pago no devolvió la referencia externa");
        }

        String trimmed = externalReference.trim();
        if (trimmed.isEmpty()) {
            throw new BusinessException("La referencia externa no contiene el identificador del alquiler");
        }

        if (trimmed.startsWith(ALQUILER_REFERENCE_PREFIX)) {
            String id = trimmed.substring(ALQUILER_REFERENCE_PREFIX.length()).trim();
            if (!id.isEmpty()) {
                return id;
            }
            throw new BusinessException("La referencia externa no contiene el identificador del alquiler");
        }

        return trimmed;
    }

    private Vehiculo obtenerVehiculoDesdeServicio(Alquiler alquiler) {
        if (alquiler.getVehiculo() == null || alquiler.getVehiculo().getId() == null
            || alquiler.getVehiculo().getId().isBlank()) {
            throw new BusinessException("El alquiler no tiene un vehículo asociado");
        }
        return obtenerVehiculo(alquiler.getVehiculo().getId());
    }

    private long calcularDiasDeAlquiler(Alquiler alquiler) {
        LocalDate desde = alquiler.getFechaDesde();
        LocalDate hasta = alquiler.getFechaHasta();
        if (desde == null || hasta == null) {
            throw new BusinessException("No se pudo determinar la duración del alquiler");
        }
        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;
        if (dias <= 0) {
            throw new BusinessException("La duración del alquiler es inválida");
        }
        return dias;
    }

    private double resolverCostoDiario(Vehiculo vehiculo) {
        if (vehiculo.getCaracteristicaVehiculo() == null
            || vehiculo.getCaracteristicaVehiculo().getId() == null
            || vehiculo.getCaracteristicaVehiculo().getId().isBlank()) {
            throw new BusinessException("El vehículo no tiene características asociadas");
        }
        CostoVehiculo costoVehiculo = costoVehiculoService.obtenerCostoVigente(
            vehiculo.getCaracteristicaVehiculo().getId());
        if (costoVehiculo == null || costoVehiculo.getCosto() == null) {
            throw new BusinessException("No se pudo determinar el costo diario del vehículo");
        }
        double costo = costoVehiculo.getCosto();
        if (costo <= 0) {
            throw new BusinessException("El costo diario del vehículo debe ser mayor a cero");
        }
        return costo;
    }

    private double calcularTotalAlquiler(double costoDiario, long diasDeAlquiler) {
        if (diasDeAlquiler <= 0) {
            throw new BusinessException("La duración del alquiler es inválida");
        }
        if (costoDiario <= 0) {
            throw new BusinessException("El costo diario del vehículo debe ser mayor a cero");
        }
        BigDecimal total = BigDecimal.valueOf(costoDiario)
            .multiply(BigDecimal.valueOf(diasDeAlquiler));
        return sanitizeAmount(total).doubleValue();
    }

    private Factura crearFacturaParaAlquiler(Long numeroFactura, long diasDeAlquiler, double total, Alquiler alquiler) {
        Factura factura = new Factura();
        factura.setNumeroFactura(numeroFactura);
        factura.setFechaFactura(LocalDate.now());
        factura.setTotalPagado(total);
        factura.setEstado(EstadoFactura.PAGADA);

        DetalleFactura detalle = new DetalleFactura();
        detalle.setCantidad(convertirDiasAEntero(diasDeAlquiler));
        detalle.setSubtotal(total);
        detalle.setAlquiler(alquiler);
        detalle.setFactura(factura);

        List<DetalleFactura> detalles = new ArrayList<>();
        detalles.add(detalle);
        factura.setDetalles(detalles);

        return facturaService.alta(factura);
    }

    private int convertirDiasAEntero(long diasDeAlquiler) {
        if (diasDeAlquiler <= 0) {
            throw new BusinessException("La cantidad de días del alquiler debe ser mayor a cero");
        }
        if (diasDeAlquiler > Integer.MAX_VALUE) {
            throw new BusinessException("La cantidad de días del alquiler es demasiado grande");
        }
        return (int) diasDeAlquiler;
    }

    public Optional<Factura> processPaymentNotification(String paymentId, String preferenceId) {
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

        String resolvedPaymentId = payment.getId() != null ? payment.getId().toString() : paymentId;
        Factura factura = processSuccessfulPayment(resolvedPaymentId, externalReference);
        return Optional.ofNullable(factura);
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
}

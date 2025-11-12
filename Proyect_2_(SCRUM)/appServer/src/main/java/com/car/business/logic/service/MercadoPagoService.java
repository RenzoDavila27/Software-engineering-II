package com.car.business.logic.service;

import com.car.business.domain.Cliente;
import com.car.business.domain.CostoVehiculo;
import com.car.business.domain.Usuario;
import com.car.business.domain.Vehiculo;
import com.car.business.logic.error.BusinessException;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

@Service
public class MercadoPagoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MercadoPagoService.class);
    private static final String REFERENCE_PREFIX = "ALQUILER";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final String accessToken;
    private final VehiculoService vehiculoService;
    private final UsuarioService usuarioService;
    private final CostoVehiculoService costoVehiculoService;

    public MercadoPagoService(@Value("${mercadopago.access-token:}") String accessToken,
        VehiculoService vehiculoService, UsuarioService usuarioService, CostoVehiculoService costoVehiculoService) {
        this.accessToken = accessToken;
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

        Cliente cliente = obtenerClienteAutenticado();
        Vehiculo vehiculo = obtenerVehiculo(request.vehiculoId());
        validarFechas(request.fechaDesde(), request.fechaHasta());

        long dias = calcularDias(request.fechaDesde(), request.fechaHasta());
        BigDecimal monto = calcularMonto(vehiculo, dias);

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
            .title(request.title())
            .description(request.description())
            .quantity(1)
            .currencyId(resolveCurrency(request.currencyId()))
            .unitPrice(monto)
            .build();

        String externalReference = buildExternalReference(cliente.getId(), vehiculo.getId(),
            request.fechaDesde(), request.fechaHasta(), dias, monto);

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
        // Aquí podría dispararse un evento o registrarse el pago para que otro flujo cree el alquiler/factura
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
        long dias, BigDecimal monto) {
        if (clienteId == null || clienteId.isBlank()) {
            throw new BusinessException("No se pudo determinar el cliente del pago");
        }
        return String.join("|",
            REFERENCE_PREFIX,
            clienteId,
            vehiculoId,
            DATE_FORMATTER.format(desde),
            DATE_FORMATTER.format(hasta),
            String.valueOf(dias),
            monto.toPlainString()
        );
    }

    private PreferenceMetadata parseExternalReference(String externalReference) {
        if (externalReference == null || externalReference.isBlank()) {
            throw new BusinessException("Mercado Pago no devolvió la referencia externa");
        }

        String trimmed = externalReference.trim();
        String[] parts = trimmed.split("\\|");
        if (parts.length < 7 || !REFERENCE_PREFIX.equalsIgnoreCase(parts[0])) {
            throw new BusinessException("La referencia externa no contiene la información esperada");
        }
        String clienteId = parts[1];
        String vehiculoId = parts[2];
        LocalDate desde = LocalDate.parse(parts[3], DATE_FORMATTER);
        LocalDate hasta = LocalDate.parse(parts[4], DATE_FORMATTER);
        long dias = Long.parseLong(parts[5]);
        BigDecimal monto = sanitizeAmount(new BigDecimal(parts[6]));
        return new PreferenceMetadata(clienteId, vehiculoId, desde, hasta, dias, monto);
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
        BigDecimal monto) {
    }
}

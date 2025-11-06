package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.CuotaMensual;
import com.fioritech.gimnasio.business.domain.Factura;
import com.fioritech.gimnasio.business.domain.FormaDePago;
import com.fioritech.gimnasio.business.domain.enums.EstadoCuotaMensual;
import com.fioritech.gimnasio.business.domain.enums.EstadoFactura;
import com.fioritech.gimnasio.business.domain.enums.TipoPago;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.controller.api.dto.MercadoPagoPreferenceRequest;
import com.fioritech.gimnasio.controller.api.dto.MercadoPagoPreferenceResponse;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.net.MPSearchRequest;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MercadoPagoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MercadoPagoService.class);

    private final String accessToken;
    private final FacturaService facturaService;
    private final CuotaMensualService cuotaMensualService;
    private final FormaDePagoService formaDePagoService;

    public MercadoPagoService(@Value("${mercadopago.access-token:}") String accessToken,
        FacturaService facturaService, CuotaMensualService cuotaMensualService,
        FormaDePagoService formaDePagoService) {
        this.accessToken = accessToken;
        this.facturaService = facturaService;
        this.cuotaMensualService = cuotaMensualService;
        this.formaDePagoService = formaDePagoService;
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

        String currency = resolveCurrency(request.currencyId());
        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
            .title(request.title())
            .description(request.description())
            .quantity(request.quantity())
            .currencyId(currency)
            .unitPrice(sanitizeAmount(request.unitPrice()))
            .build();

        PreferenceRequest.PreferenceRequestBuilder preferenceBuilder = PreferenceRequest.builder()
            .items(List.of(itemRequest));

        if (request.externalReference() != null && !request.externalReference().isBlank()) {
            preferenceBuilder.externalReference(request.externalReference());
        }

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
                request.title(), request.unitPrice(), request.externalReference(), backUrls);
        }
        try {
            Preference preference = client.create(preferenceRequest);
            return new MercadoPagoPreferenceResponse(preference.getId(), preference.getInitPoint(),
                preference.getSandboxInitPoint());
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

    @Transactional
    public Factura processSuccessfulPayment(String paymentId, String externalReference) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new BusinessException("Mercado Pago no devolvió un identificador de pago válido");
        }

        List<String> cuotaIds = extractCuotaIds(externalReference);
        if (cuotaIds.isEmpty()) {
            throw new BusinessException("No se pudo determinar qué cuotas fueron abonadas");
        }

        List<CuotaMensual> cuotas = cuotaIds.stream().map(cuotaMensualService::buscarCuotaMensual).toList();

        Set<String> socioIds = cuotas.stream().map(c -> c.getSocio().getId()).collect(Collectors.toSet());
        if (socioIds.size() != 1) {
            throw new BusinessException("Las cuotas seleccionadas pertenecen a distintos socios");
        }

        boolean todasPagas = cuotas.stream()
            .allMatch(c -> c.getEstado() == EstadoCuotaMensual.PAGADA);
        if (todasPagas) {
            throw new BusinessException("Las cuotas seleccionadas ya se encuentran pagadas");
        }

        boolean algunaPagada = cuotas.stream()
            .anyMatch(c -> c.getEstado() == EstadoCuotaMensual.PAGADA);
        if (algunaPagada) {
            throw new BusinessException("Hay cuotas seleccionadas que ya fueron abonadas");
        }

        double total = cuotas.stream()
            .mapToDouble(c -> c.getValorCuota().getValorCuota())
            .sum();

        FormaDePago formaDePago = formaDePagoService.buscarPorTipo(TipoPago.BILLETERA_VIRTUAL);

        Long numeroFactura = resolveInvoiceNumber(paymentId);

        Factura factura = facturaService.crearFactura(
            numeroFactura,
            LocalDate.now(),
            total,
            EstadoFactura.PAGADA,
            cuotas.get(0).getSocio().getId(),
            formaDePago.getId(),
            new ArrayList<>(cuotaIds)
        );

        cuotas.forEach(cuota -> cuotaMensualService.modificarCuota(cuota.getId(), null, null,
            EstadoCuotaMensual.PAGADA));

        return factura;
    }

    private Long resolveInvoiceNumber(String paymentId) {
        try {
            return Long.parseLong(paymentId);
        } catch (NumberFormatException ex) {
            LOGGER.warn("El payment_id '{}' no es numérico. Se generará un número alternativo.", paymentId);
            return System.currentTimeMillis();
        }
    }

    private List<String> extractCuotaIds(String externalReference) {
        if (externalReference == null || externalReference.isBlank()) {
            throw new BusinessException("Mercado Pago no devolvió la referencia externa");
        }

        String delimiterRegex = externalReference.contains("~") ? "~" : "\\|";
        String[] partes = externalReference.split(delimiterRegex);
        if (partes.length < 3) {
            throw new BusinessException("La referencia externa no contiene la información esperada");
        }

        if (!"CUOTAS".equalsIgnoreCase(partes[0])) {
            throw new BusinessException("La referencia externa no corresponde a un pago de cuotas");
        }

        return Arrays.stream(partes[2].split(","))
            .map(String::trim)
            .filter(id -> !id.isBlank())
            .distinct()
            .collect(Collectors.toCollection(ArrayList::new));
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

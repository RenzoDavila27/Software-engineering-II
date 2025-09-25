package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.controller.api.dto.MercadoPagoPreferenceRequest;
import com.fioritech.gimnasio.controller.api.dto.MercadoPagoPreferenceResponse;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MercadoPagoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MercadoPagoService.class);

    private final String accessToken;

    public MercadoPagoService(@Value("${mercadopago.access-token:}") String accessToken) {
        this.accessToken = accessToken;
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

        PreferenceRequest preferenceRequest = preferenceBuilder.build();

        PreferenceClient client = new PreferenceClient();
        try {
            Preference preference = client.create(preferenceRequest);
            return new MercadoPagoPreferenceResponse(preference.getId(), preference.getInitPoint(),
                preference.getSandboxInitPoint());
        } catch (MPApiException | MPException ex) {
            LOGGER.error("Error al crear preferencia en Mercado Pago", ex);
            throw new BusinessException("No fue posible crear la preferencia de pago en Mercado Pago");
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
}

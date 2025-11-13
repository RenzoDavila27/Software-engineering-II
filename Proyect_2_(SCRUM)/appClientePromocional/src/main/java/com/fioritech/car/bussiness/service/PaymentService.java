package com.fioritech.car.bussiness.service;

import com.fioritech.car.bussiness.dto.*;
import com.fioritech.car.bussiness.repository.AlquilerRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final DateTimeFormatter HUMAN_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AlquilerRepository alquilerRepository;
    private final VehiculoService vehiculoService;
    private final WebClient.Builder webClientBuilder;

    @Value("${app.server.base-url:http://localhost:8080}")
    private String appServerBaseUrl;

    @Value("${app.mercadopago.notification-url:https://arrantly-nonperturbing-darlena.ngrok-free.dev/mercadopago/webhook}")
    private String mercadoPagoNotificationUrl;

    public Mono<AlquilerDto> processPayment(String vehiculoId, int rentalDays, double totalPrice,
                                                    LocalDate fechaDesde, LocalDate fechaHasta,
                                                    String authorizationHeader,
                                                    DocumentoAdjuntoDto docDni,
                                                    DocumentoAdjuntoDto docLicencia) {
        System.out.println("Processing Efectivo Payment:");
        System.out.println("  Vehiculo ID: " + vehiculoId);
        System.out.println("  Rental Days: " + rentalDays);
        System.out.println("  Total Price: " + totalPrice);
        System.out.println("  Period: " + fechaDesde + " to " + fechaHasta);
        System.out.println("  Order registered. Payment due on pickup day.");

        PaymentRequest request = new PaymentRequest(vehiculoId, fechaDesde, fechaHasta, totalPrice, docDni, docLicencia);

        return webClientBuilder.baseUrl(appServerBaseUrl).build()
                .post()
                .uri("/api/payment")
                .headers(headers -> {
                    if (StringUtils.hasText(authorizationHeader)) {
                        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
                    }
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AlquilerDto.class);
    }

    public Mono<String> processMercadoPagoPayment(String vehiculoId, int rentalDays, double totalPrice,
                                                  LocalDate fechaDesde, LocalDate fechaHasta, String returnBaseUrl,
                                                  String authorizationHeader,
                                                  DocumentoAdjuntoDto docDni,
                                                  DocumentoAdjuntoDto docLicencia) {
        System.out.println("Processing Mercado Pago Payment:");
        System.out.println("  Vehiculo ID: " + vehiculoId);
        System.out.println("  Rental Days: " + rentalDays);
        System.out.println("  Total Price: " + totalPrice);
        System.out.println("  Period: " + fechaDesde + " to " + fechaHasta);
        System.out.println("  Creating preference via appServer");
        return vehiculoService.findById(vehiculoId)
                .switchIfEmpty(Mono.error(() -> new IllegalArgumentException("Vehículo no encontrado")))
                .map(vehiculoDto -> buildPreferenceRequest(
                        vehiculoDto, vehiculoId, rentalDays, fechaDesde, fechaHasta, returnBaseUrl, docDni, docLicencia
                ))
            .flatMap(request -> webClientBuilder.baseUrl(appServerBaseUrl).build()
                .post()
                .uri("/api/mercadopago/preferences")
                .headers(headers -> {
                    if (StringUtils.hasText(authorizationHeader)) {
                        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
                    }
                })
                .bodyValue(request)
                .retrieve()
                        .bodyToMono(MercadoPagoPreferenceResponse.class))
                .map(response -> response.getInitPoint() != null
                        ? response.getInitPoint()
                        : response.getSandboxInitPoint());
    }

    private MercadoPagoPreferenceRequest buildPreferenceRequest(VehiculoDto vehiculoDto,
                                                                String vehiculoId,
                                                                int rentalDays,
                                                                LocalDate fechaDesde,
                                                                LocalDate fechaHasta,
                                                                String returnBaseUrl,
                                                                DocumentoAdjuntoDto docDni,
                                                                DocumentoAdjuntoDto docLicencia) {
        String title = vehiculoDto.getCaracteristicaVehiculo().getMarca() + " "
            + vehiculoDto.getCaracteristicaVehiculo().getModelo();
        String description = String.format("Alquiler de %s del %s al %s (%d días)",
            title,
            fechaDesde.format(HUMAN_DATE),
            fechaHasta.format(HUMAN_DATE),
            rentalDays);

        return new MercadoPagoPreferenceRequest(
            title,
            description,
            "ARS",
            returnBaseUrl,
            returnBaseUrl,
            returnBaseUrl,
            "approved",
            mercadoPagoNotificationUrl,
            vehiculoId,
            fechaDesde,
            fechaHasta,
            docDni,
            docLicencia
        );
    }
}

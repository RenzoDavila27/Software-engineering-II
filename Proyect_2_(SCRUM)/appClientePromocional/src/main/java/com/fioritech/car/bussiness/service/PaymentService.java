package com.fioritech.car.bussiness.service;

import com.fioritech.car.bussiness.dto.AlquilerDto;
import com.fioritech.car.bussiness.dto.MercadoPagoPreferenceRequest;
import com.fioritech.car.bussiness.dto.MercadoPagoPreferenceResponse;
import com.fioritech.car.bussiness.dto.VehiculoDto;
import com.fioritech.car.bussiness.repository.AlquilerRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final DateTimeFormatter HUMAN_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AlquilerRepository alquilerRepository;
    private final VehiculoService vehiculoService;
    private final WebClient.Builder webClientBuilder;

    @Value("${app.server.base-url:http://localhost:8081}")
    private String appServerBaseUrl;

    @Value("${app.mercadopago.notification-url:http://localhost:8081/mercadopago/webhook}")
    private String mercadoPagoNotificationUrl;

    public Mono<AlquilerDto> processEfectivoPayment(String vehiculoId, int rentalDays, double totalPrice,
                                                    LocalDate fechaDesde, LocalDate fechaHasta) {
        System.out.println("Processing Efectivo Payment:");
        System.out.println("  Vehiculo ID: " + vehiculoId);
        System.out.println("  Rental Days: " + rentalDays);
        System.out.println("  Total Price: " + totalPrice);
        System.out.println("  Period: " + fechaDesde + " to " + fechaHasta);
        System.out.println("  Order registered. Payment due on pickup day.");

        return createAndSaveAlquiler(vehiculoId, fechaDesde, fechaHasta);
    }

    public Mono<AlquilerDto> processTransferenciaPayment(String vehiculoId, int rentalDays, double totalPrice,
                                                         LocalDate fechaDesde, LocalDate fechaHasta) {
        System.out.println("Processing Transferencia Bancaria Payment:");
        System.out.println("  Vehiculo ID: " + vehiculoId);
        System.out.println("  Rental Days: " + rentalDays);
        System.out.println("  Total Price: " + totalPrice);
        System.out.println("  Period: " + fechaDesde + " to " + fechaHasta);
        System.out.println("  Transfer details: Alias: mycar.mp, CBU: 123456789, Banco: BancoFioriTech");

        return createAndSaveAlquiler(vehiculoId, fechaDesde, fechaHasta);
    }

    public Mono<String> processMercadoPagoPayment(String vehiculoId, int rentalDays, double totalPrice,
                                                  LocalDate fechaDesde, LocalDate fechaHasta, String returnBaseUrl) {
        System.out.println("Processing Mercado Pago Payment:");
        System.out.println("  Vehiculo ID: " + vehiculoId);
        System.out.println("  Rental Days: " + rentalDays);
        System.out.println("  Total Price: " + totalPrice);
        System.out.println("  Period: " + fechaDesde + " to " + fechaHasta);
        System.out.println("  Creating preference via appServer");

        return vehiculoService.findById(vehiculoId)
            .switchIfEmpty(Mono.error(() -> new IllegalArgumentException("Vehículo no encontrado")))
            .map(vehiculoDto -> buildPreferenceRequest(vehiculoDto, vehiculoId, rentalDays, fechaDesde, fechaHasta, returnBaseUrl))
            .flatMap(request -> webClientBuilder.baseUrl(appServerBaseUrl).build()
                .post()
                .uri("/api/mercadopago/preferences")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MercadoPagoPreferenceResponse.class))
            .map(response -> response.getInitPoint() != null ? response.getInitPoint() : response.getSandboxInitPoint());
    }

    private MercadoPagoPreferenceRequest buildPreferenceRequest(VehiculoDto vehiculoDto,
                                                                String vehiculoId,
                                                                int rentalDays,
                                                                LocalDate fechaDesde,
                                                                LocalDate fechaHasta,
                                                                String returnBaseUrl) {
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
            returnBaseUrl + "/success",
            returnBaseUrl + "/success",
            returnBaseUrl + "/success",
            "approved",
            mercadoPagoNotificationUrl,
            vehiculoId,
            fechaDesde,
            fechaHasta
        );
    }

    private Mono<AlquilerDto> createAndSaveAlquiler(String vehiculoId, LocalDate fechaDesde, LocalDate fechaHasta) {
        return vehiculoService.findById(vehiculoId)
            .flatMap(vehiculoDto -> {
                AlquilerDto alquilerDto = new AlquilerDto();
                alquilerDto.setFechaInicio(toDate(fechaDesde));
                alquilerDto.setFechaFin(toDate(fechaHasta));
                alquilerDto.setVehiculo(vehiculoDto);
                System.out.println("Attempting to save AlquilerDto: " + alquilerDto);
                return alquilerRepository.saveAlquiler(alquilerDto);
            });
    }

    private Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}

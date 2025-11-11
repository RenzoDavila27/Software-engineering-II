package com.fioritech.car.bussiness.service;

import com.fioritech.car.bussiness.dto.AlquilerDto;
import com.fioritech.car.bussiness.dto.VehiculoDto; // Assuming VehiculoDto is needed for AlquilerDto
import com.fioritech.car.bussiness.repository.AlquilerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date; // For setting dates in AlquilerDto

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AlquilerRepository alquilerRepository;
    private final VehiculoService vehiculoService; // To get VehiculoDto details

    public Mono<AlquilerDto> processEfectivoPayment(String vehiculoId, int rentalDays, double totalPrice) {
        System.out.println("Processing Efectivo Payment:");
        System.out.println("  Vehiculo ID: " + vehiculoId);
        System.out.println("  Rental Days: " + rentalDays);
        System.out.println("  Total Price: " + totalPrice);
        System.out.println("  Order registered. Payment due on pickup day.");

        return createAndSaveAlquiler(vehiculoId, rentalDays, totalPrice, "Efectivo");
    }

    public Mono<AlquilerDto> processTransferenciaPayment(String vehiculoId, int rentalDays, double totalPrice) {
        System.out.println("Processing Transferencia Bancaria Payment:");
        System.out.println("  Vehiculo ID: " + vehiculoId);
        System.out.println("  Rental Days: " + rentalDays);
        System.out.println("  Total Price: " + totalPrice);
        System.out.println("  Transfer details: Alias: mycar.mp, CBU: 123456789, Banco: BancoFioriTech");

        return createAndSaveAlquiler(vehiculoId, rentalDays, totalPrice, "Transferencia Bancaria");
    }

    public Mono<AlquilerDto> processMercadoPagoPayment(String vehiculoId, int rentalDays, double totalPrice) {
        System.out.println("Processing Mercado Pago Payment:");
        System.out.println("  Vehiculo ID: " + vehiculoId);
        System.out.println("  Rental Days: " + rentalDays);
        System.out.println("  Total Price: " + totalPrice);
        System.out.println("  Redirecting to Mercado Pago for payment.");

        return createAndSaveAlquiler(vehiculoId, rentalDays, totalPrice, "Mercado Pago");
    }

    private Mono<AlquilerDto> createAndSaveAlquiler(String vehiculoId, int rentalDays, double totalPrice, String paymentMethod) {
        // In a real application, you would get the actual start and end dates,
        // and the authenticated user details.
        // For now, we'll use placeholder dates and a dummy user.

        return vehiculoService.findById(vehiculoId)
                .flatMap(vehiculoDto -> {
                    AlquilerDto alquilerDto = new AlquilerDto();
                    // Set placeholder dates for now
                    alquilerDto.setFechaInicio(new Date());
                    alquilerDto.setFechaFin(new Date(System.currentTimeMillis() + (long) rentalDays * 24 * 60 * 60 * 1000));
                    alquilerDto.setVehiculo(vehiculoDto); // Set the full VehiculoDto
                    // TODO: Set actual authenticated user
                    // alquilerDto.setUsuario(currentUserDto);

                    System.out.println("Attempting to save AlquilerDto: " + alquilerDto);
                    return alquilerRepository.saveAlquiler(alquilerDto);
                });
    }
}

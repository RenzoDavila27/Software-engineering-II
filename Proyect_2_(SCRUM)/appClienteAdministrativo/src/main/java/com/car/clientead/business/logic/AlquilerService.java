package com.car.clientead.business.logic;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.car.clientead.client.dto.AlquilerDto;
import com.car.clientead.client.dto.DocumentacionDto;
import com.car.clientead.client.dto.enums.TipoDocumentacion;
import com.car.clientead.client.exception.ApiClientException;
import com.car.clientead.repository.AlquilerRepository;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository repository;

    @Autowired
    private DocumentacionService documentacionService;

    public List<AlquilerDto> listar() {
        return repository.findAll().stream()
                .filter(this::registroValido)
                .collect(Collectors.toList());
    }

    public List<AlquilerDto> listarPorCliente(String clienteId) {
        if (!StringUtils.hasText(clienteId)) {
            return Collections.emptyList();
        }
        return repository.findAll().stream()
                .filter(this::registroValido)
                .filter(dto -> clienteId.equals(dto.getClienteId()))
                .collect(Collectors.toList());
    }

    public AlquilerDto consultar(String id) {
        return repository.findById(id);
    }

    public AlquilerDto crear(AlquilerDto dto,
                              TipoDocumentacion tipoDocumentacion,
                              String observacionDoc,
                              MultipartFile[] archivosDocumentacion) {
        validar(dto);
        DocumentacionDto documentacion = registrarDocumentacion(tipoDocumentacion, observacionDoc, archivosDocumentacion);
        dto.setDocumentacionId(documentacion.getId());
        return repository.create(dto);
    }

    public AlquilerDto modificar(String id,
                                  AlquilerDto dto,
                                  TipoDocumentacion tipoDocumentacion,
                                  String observacionDoc,
                                  MultipartFile[] archivosDocumentacion) {
        validar(dto);
        if (archivosDocumentacion != null && Arrays.stream(archivosDocumentacion).anyMatch(file -> file != null && !file.isEmpty())) {
            // Registrar nueva documentación y eliminar la anterior
            DocumentacionDto nueva = registrarDocumentacion(tipoDocumentacion, observacionDoc, archivosDocumentacion);
            if (StringUtils.hasText(dto.getDocumentacionId())) {
                try {
                    documentacionService.eliminar(dto.getDocumentacionId());
                } catch (Exception ignored) {
                }
            }
            dto.setDocumentacionId(nueva.getId());
        }
        return repository.update(id, dto);
    }

    public void eliminar(String id) {
        repository.delete(id);
    }

    private DocumentacionDto registrarDocumentacion(TipoDocumentacion tipo,
                                                    String observacion,
                                                    MultipartFile[] archivosDocumentacion) {
        List<MultipartFile> archivos = archivosDocumentacion == null
                ? Collections.emptyList()
                : Arrays.stream(archivosDocumentacion)
                        .filter(file -> file != null && !file.isEmpty())
                        .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(archivos)) {
            throw new IllegalArgumentException("Debe adjuntar la documentación del cliente en formato PDF o Word.");
        }
        return documentacionService.registrar(
                tipo != null ? tipo : TipoDocumentacion.DOCUMENTO_IDENTIDAD,
                observacion,
                archivos);
    }

    private void validar(AlquilerDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos del alquiler no pueden ser nulos.");
        }
        if (!StringUtils.hasText(dto.getClienteId())) {
            throw new IllegalArgumentException("Debe seleccionar un cliente.");
        }
        if (!StringUtils.hasText(dto.getVehiculoId())) {
            throw new IllegalArgumentException("Debe seleccionar un vehículo.");
        }
        LocalDate desde = dto.getFechaDesde();
        LocalDate hasta = dto.getFechaHasta();
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Debe indicar la fecha de inicio y fin del alquiler.");
        }
        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha de devolución no puede ser anterior a la fecha de retiro.");
        }
    }

    private boolean registroValido(AlquilerDto dto) {
        return dto != null && StringUtils.hasText(dto.getClienteId()) && StringUtils.hasText(dto.getVehiculoId());
    }
}

package com.car.business.logic.service;

import com.car.business.domain.Alquiler;
import com.car.business.domain.Cliente;
import com.car.business.domain.DetalleFactura;
import com.car.business.domain.Documentacion;
import com.car.business.domain.Factura;
import com.car.business.domain.Vehiculo;
import com.car.business.domain.enums.EstadoFactura;
import com.car.business.domain.enums.EstadoVehiculo;
import com.car.business.domain.enums.TipoDocumentacion;
import com.car.business.dto.AlquilerDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.AlquilerMapper;
import com.car.business.percistence.repository.AlquilerRepository;
import com.car.controller.rest.api.dto.DocumentoAdjuntoDto;
import com.car.controller.rest.api.dto.PaymentRequest;
import com.car.seguridad.authentication.UserPrincipal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlquilerService extends BaseService<Alquiler, AlquilerDto, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlquilerService.class);

    private final VehiculoService vehiculoService;
    private final CaracteristicaVehiculoService caracteristicaVehiculoService;
    private final AlquilerRepository alquilerRepository;
    private final FacturaService facturaService;
    private final DetalleFacturaService detalleFacturaService;
    private final DocumentacionService documentacionService;
    private final ClienteService clienteService;

    @Autowired
    public AlquilerService(AlquilerRepository repository, AlquilerMapper mapper, VehiculoService vehiculoService,
                           CaracteristicaVehiculoService caracteristicaVehiculoService,
                           AlquilerRepository alquilerRepository, FacturaService facturaService,
                           DetalleFacturaService detalleFacturaService, DocumentacionService documentacionService,
                           ClienteService clienteService) {
        super(repository, mapper);
        this.vehiculoService = vehiculoService;
        this.caracteristicaVehiculoService = caracteristicaVehiculoService;
        this.alquilerRepository = alquilerRepository;
        this.facturaService = facturaService;
        this.detalleFacturaService = detalleFacturaService;
        this.documentacionService = documentacionService;
        this.clienteService = clienteService;
    }

    @Transactional
    public Alquiler processPayment(PaymentRequest request) throws BusinessException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cliente cliente = clienteService.findByUserId(userPrincipal.getId()).get();

        if (cliente == null) {
            throw new BusinessException("No se pudo determinar el cliente autenticado");
        }

        DocumentosGuardados documentosGuardados = guardarDocumentosTemporales(request.getDocDni(), request.getDocLicencia(), cliente.getId());
        Documentacion documentacion = registrarDocumentacionDesdePaths(documentosGuardados);

        Vehiculo vehiculo = vehiculoService.obtenerVehiculo(request.getVehiculoId()).get();

        if (vehiculo == null) {
            throw new BusinessException("No se encontro el vehiculo");
        }

        Alquiler alquiler = new Alquiler();
        alquiler.setCliente(cliente);
        alquiler.setVehiculo(vehiculo);
        alquiler.setFechaDesde(request.getFechaDesde());
        alquiler.setFechaHasta(request.getFechaHasta());
        alquiler.setDocumentacion(documentacion);
        this.alta(alquiler);

        Factura factura = new Factura();
        factura.setNumeroFactura(System.currentTimeMillis());
        factura.setFechaFactura(LocalDate.now());
        factura.setEstado(EstadoFactura.SIN_DEFINIR);
        factura.setTotalPagado(request.getTotalPrice());

        DetalleFactura detalleFactura = new DetalleFactura();
        detalleFactura.setAlquiler(alquiler);
        detalleFactura.setFactura(factura);
        detalleFactura.setSubtotal(request.getTotalPrice());
        detalleFactura.setCantidad((int) ChronoUnit.DAYS.between(request.getFechaDesde(), request.getFechaHasta()));

        List<DetalleFactura> detalles = new ArrayList<>();
        detalles.add(detalleFactura);
        factura.setDetalles(detalles);
        
        facturaService.alta(factura);
        detalleFacturaService.alta(detalleFactura);

        LOGGER.info("Alquiler {} y factura registrados.", alquiler.getId());
        return alquiler;
    }

    private DocumentosGuardados guardarDocumentosTemporales(DocumentoAdjuntoDto docDni,
                                                                               DocumentoAdjuntoDto docLicencia, String clienteId) {
        if (docDni == null || docLicencia == null) {
            throw new BusinessException("Los documentos del cliente son obligatorios para completar la reserva");
        }
        try {
            String clienteFolder = sanitizeFileName(clienteId);
            String carpetaAlquiler = "alquiler-" + UUID.randomUUID();
            Path targetDir = Paths.get(new Documentacion().getPathArchivo(), clienteFolder, carpetaAlquiler);
            Files.createDirectories(targetDir);

            Path dniPath = guardarArchivoDocumento(docDni, targetDir, "dni");
            Path licenciaPath = guardarArchivoDocumento(docLicencia, targetDir, "licencia");

            return new DocumentosGuardados(targetDir, dniPath, licenciaPath);
        } catch (IOException | IllegalArgumentException ex) {
            LOGGER.error("Error almacenando la documentación del cliente {}", clienteId, ex);
            throw new BusinessException("No fue posible almacenar la documentación del cliente. Intentalo nuevamente.");
        }
    }

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

    private Documentacion registrarDocumentacionDesdePaths(DocumentosGuardados documentosGuardados) {
        Path dniPath = documentosGuardados.dniPath();
        Path licenciaPath = documentosGuardados.licenciaPath();
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

    @Override
    protected void validar(Alquiler entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El alquiler es obligatorio.");
        }
        if (entidad.getCliente() == null) {
            throw new BusinessException("El cliente del alquiler es obligatorio.");
        }
        if (entidad.getVehiculo().getEstadoVehiculo()==EstadoVehiculo.ALQUILADO) {
            throw new BusinessException("El Vehiculo ya esta alquilado");
        }
        LocalDate fechaDesde = entidad.getFechaDesde();
        if (fechaDesde == null) {
            throw new BusinessException("La fecha desde es obligatoria.");
        }
        LocalDate fechaHasta = entidad.getFechaHasta();
        if (fechaHasta == null) {
            throw new BusinessException("La fecha hasta es obligatoria.");
        }
        if (entidad.getDocumentacion() == null) {
            throw new BusinessException("La documentación asociada es obligatoria.");
        }
        if (entidad.getVehiculo() == null) {
            throw new BusinessException("El vehículo es obligatorio.");
        }
        if (fechaHasta.isBefore(fechaDesde)) {
            throw new BusinessException("La fecha hasta no puede ser anterior a la fecha desde.");
        }
    }

    @Override
    protected void postAlta(Alquiler alquiler) throws BusinessException{
        try{
            vehiculoService.cambiarEstadoVehiculo(alquiler.getVehiculo().getId(),EstadoVehiculo.ALQUILADO);
            caracteristicaVehiculoService.sumarCantVehiculoAlquilado(alquiler.getVehiculo().getCaracteristicaVehiculo().getId());
        }catch(BusinessException e){
            throw e;
        }
    }

    public List<Alquiler> buscarAlquileresVecManiana(LocalDate maniana) throws BusinessException{
        return alquilerRepository.buscarAlquilerVecManiana(maniana);
    }

    public void marcarEntrega(String alquilerId) throws BusinessException {
        Alquiler alquiler = repository.findById(alquilerId)
                .orElseThrow(() -> new BusinessException("No existe el alquiler solicitado."));

        vehiculoService.cambiarEstadoVehiculo(alquiler.getVehiculo().getId(),EstadoVehiculo.DISPONIBLE);
        caracteristicaVehiculoService.restarCantVehiculoAlquilado(alquiler.getVehiculo().getCaracteristicaVehiculo().getId());
    }

    public List<Alquiler> buscarAlquileresEmpHoy(LocalDate hoy) throws BusinessException{
        return alquilerRepository.buscarAlquilerEmpHoy(hoy);
    }


    public void marcarEntregaError(String alquilerId) throws BusinessException{
         Alquiler alquiler = repository.findById(alquilerId)
                .orElseThrow(() -> new BusinessException("No existe el alquiler solicitado."));

        vehiculoService.cambiarEstadoVehiculo(alquiler.getVehiculo().getId(),EstadoVehiculo.NO_DISPONIBLE);
        caracteristicaVehiculoService.restarCantVehiculoAlquilado(alquiler.getVehiculo().getCaracteristicaVehiculo().getId());   
    }
}


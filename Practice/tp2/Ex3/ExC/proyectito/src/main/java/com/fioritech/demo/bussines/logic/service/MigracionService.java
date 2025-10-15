package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.domain.Direccion;
import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.domain.Proveedor;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.dto.MigracionResultado;
import com.fioritech.demo.bussines.logic.service.template.OperationTemplateService;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.DepartamentoRepository;
import com.fioritech.demo.bussines.repository.LocalidadRepository;
import com.fioritech.demo.bussines.repository.PaisRepository;
import com.fioritech.demo.bussines.repository.ProvinciaRepository;
import com.fioritech.demo.bussines.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class MigracionService extends OperationTemplateService<MultipartFile, MigracionResultado> {

    private static final int CAMPOS_ESPERADOS = 11;
    private static final String COD_POSTAL_POR_DEFECTO = "0000";

    private final PaisRepository paisRepository;
    private final ProvinciaRepository provinciaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final LocalidadRepository localidadRepository;
    private final ProveedorRepository proveedorRepository;

    private final PaisService paisService;
    private final ProvinciaService provinciaService;
    private final DepartamentoService departamentoService;
    private final LocalidadService localidadService;
    private final DireccionService direccionService;
    private final ProveedorService proveedorService;

    public MigracionService(PaisRepository paisRepository,
                            ProvinciaRepository provinciaRepository,
                            DepartamentoRepository departamentoRepository,
                            LocalidadRepository localidadRepository,
                            ProveedorRepository proveedorRepository,
                            PaisService paisService,
                            ProvinciaService provinciaService,
                            DepartamentoService departamentoService,
                            LocalidadService localidadService,
                            DireccionService direccionService,
                            ProveedorService proveedorService) {
        this.paisRepository = paisRepository;
        this.provinciaRepository = provinciaRepository;
        this.departamentoRepository = departamentoRepository;
        this.localidadRepository = localidadRepository;
        this.proveedorRepository = proveedorRepository;
        this.paisService = paisService;
        this.provinciaService = provinciaService;
        this.departamentoService = departamentoService;
        this.localidadService = localidadService;
        this.direccionService = direccionService;
        this.proveedorService = proveedorService;
    }

    public MigracionResultado migrarProveedores(MultipartFile archivo) {
        return ejecutar(archivo);
    }

    @Override
    protected void validarEntrada(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("Debe seleccionar el archivo migracion.txt");
        }
        String nombreArchivo = archivo.getOriginalFilename();
        if (nombreArchivo == null || !nombreArchivo.toLowerCase(Locale.ROOT).endsWith("migracion.txt")) {
            throw new BusinessException("El archivo debe llamarse migracion.txt");
        }
    }

    @Override
    protected MigracionResultado ejecutarOperacion(MultipartFile archivo) {
        MigracionResultado resultado = new MigracionResultado();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            int numeroLinea = 0;
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                linea = linea.replace("\uFEFF", "");
                resultado.incrementarProcesados();
                String[] columnas = linea.split(";", -1);
                if (columnas.length < CAMPOS_ESPERADOS) {
                    registrarError(resultado, numeroLinea, "La línea no contiene los " + CAMPOS_ESPERADOS + " campos requeridos");
                    continue;
                }
                try {
                    procesarRegistro(numeroLinea, columnas);
                    resultado.incrementarCreados();
                } catch (BusinessException ex) {
                    registrarError(resultado, numeroLinea, ex.getMessage());
                } catch (Exception ex) {
                    registrarError(resultado, numeroLinea, "Error inesperado: " + ex.getMessage());
                }
            }

        } catch (IOException ex) {
            throw new BusinessException("No se pudo leer el archivo de migración", ex);
        }
        return resultado;
    }

    private void procesarRegistro(int numeroLinea, String[] columnas) {
        String nombre = columnas[0].trim();
        String apellido = columnas[1].trim();
        String telefono = columnas[2].trim();
        String correo = columnas[3].trim();
        String cuit = columnas[4].trim();
        String calle = columnas[5].trim();
        String numeracion = columnas[6].trim();
        String localidadNombre = columnas[7].trim();
        String departamentoNombre = columnas[8].trim();
        String provinciaNombre = columnas[9].trim();
        String paisNombre = columnas[10].trim();

        validarCamposObligatorios(nombre, apellido, telefono, correo, cuit,
                calle, numeracion, localidadNombre, departamentoNombre, provinciaNombre, paisNombre);

        if (!ValidationUtils.isValidEmail(correo)) {
            throw new BusinessException("El correo electrónico posee un formato inválido");
        }

        Optional<Proveedor> proveedorExistente = proveedorRepository.findByCuit(cuit);
        if (proveedorExistente.isPresent()) {
            throw new BusinessException("El proveedor con CUIT " + cuit + " ya existe");
        }

        Pais pais = obtenerPais(paisNombre);
        Provincia provincia = obtenerProvincia(provinciaNombre, pais);
        Departamento departamento = obtenerDepartamento(departamentoNombre, provincia);
        Localidad localidad = obtenerLocalidad(localidadNombre, departamento);

        Direccion direccion = new Direccion();
        direccion.setCalle(calle);
        direccion.setNumeracion(numeracion);
        direccion.setLocalidad(localidad);
        direccion.setBarrio(null);
        direccion.setManzana(null);
        direccion.setCasaDepartamento(null);
        direccion.setReferencia("Migración línea " + numeroLinea);
        direccion.setEliminado(false);

        Direccion direccionPersistida = direccionService.crearDireccion(direccion);

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(nombre);
        proveedor.setApellido(apellido);
        proveedor.setTelefono(telefono);
        proveedor.setCorreo(correo);
        proveedor.setCuit(cuit);
        proveedor.setDireccion(direccionPersistida);
        proveedor.setEliminado(false);

        proveedorService.crearProveedor(proveedor);
    }

    private void validarCamposObligatorios(String... valores) {
        String[] etiquetas = {"Nombre", "Apellido", "Teléfono", "Correo", "CUIT",
                "Calle", "Número", "Localidad", "Departamento", "Provincia", "País"};
        for (int i = 0; i < valores.length; i++) {
            if (ValidationUtils.isBlank(valores[i])) {
                throw new BusinessException("El campo " + etiquetas[i] + " es obligatorio");
            }
        }
    }

    private Pais obtenerPais(String nombre) {
        return paisRepository.findByNombreIgnoreCaseAndEliminadoFalse(nombre)
                .orElseGet(() -> paisService.crearPais(nuevoPais(nombre)));
    }

    private Pais nuevoPais(String nombre) {
        Pais pais = new Pais();
        pais.setNombre(nombre);
        pais.setEliminado(false);
        return pais;
    }

    private Provincia obtenerProvincia(String nombre, Pais pais) {
        return provinciaRepository.findByNombreIgnoreCaseAndPaisAndEliminadoFalse(nombre, pais)
                .orElseGet(() -> provinciaService.crearProvincia(nuevaProvincia(nombre, pais)));
    }

    private Provincia nuevaProvincia(String nombre, Pais pais) {
        Provincia provincia = new Provincia();
        provincia.setNombre(nombre);
        provincia.setPais(pais);
        provincia.setEliminado(false);
        return provincia;
    }

    private Departamento obtenerDepartamento(String nombre, Provincia provincia) {
        return departamentoRepository.findByNombreIgnoreCaseAndProvinciaAndEliminadoFalse(nombre, provincia)
                .orElseGet(() -> departamentoService.crearDepartamento(nuevoDepartamento(nombre, provincia)));
    }

    private Departamento nuevoDepartamento(String nombre, Provincia provincia) {
        Departamento departamento = new Departamento();
        departamento.setNombre(nombre);
        departamento.setProvincia(provincia);
        departamento.setEliminado(false);
        return departamento;
    }

    private Localidad obtenerLocalidad(String nombre, Departamento departamento) {
        return localidadRepository.findByNombreIgnoreCaseAndDepartamentoAndEliminadoFalse(nombre, departamento)
                .orElseGet(() -> localidadService.crearLocalidad(nuevaLocalidad(nombre, departamento)));
    }

    private Localidad nuevaLocalidad(String nombre, Departamento departamento) {
        Localidad localidad = new Localidad();
        localidad.setNombre(nombre);
        localidad.setCodPostal(COD_POSTAL_POR_DEFECTO);
        localidad.setDepartamento(departamento);
        localidad.setEliminado(false);
        return localidad;
    }

    private void registrarError(MigracionResultado resultado, int numeroLinea, String mensaje) {
        resultado.incrementarOmitidos();
        resultado.agregarError("Línea " + numeroLinea + ": " + mensaje);
    }
}


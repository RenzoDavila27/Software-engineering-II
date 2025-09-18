package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Empleado;
import com.fioritech.gimnasio.business.domain.Sucursal;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.TipoDocumento;
import com.fioritech.gimnasio.business.domain.enums.TipoEmpleado;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.EmpleadoRepository;
import com.fioritech.gimnasio.business.persistence.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final SucursalService sucursalService;
    private final UsuarioRepository usuarioRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository, SucursalService sucursalService,
        UsuarioRepository usuarioRepository) {
        this.empleadoRepository = empleadoRepository;
        this.sucursalService = sucursalService;
        this.usuarioRepository = usuarioRepository;
    }

    public Empleado crearEmpleado(String idSucursal, String nombre, String apellido, LocalDate fechaNacimiento,
        TipoDocumento tipoDocumento, String numeroDocumento, String telefono, String correoElectronico,
        TipoEmpleado tipoEmpleado) {
        Sucursal sucursal = sucursalService.buscarSucursal(idSucursal);
        validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico,
            tipoEmpleado);
        validarDocumentoUnico(numeroDocumento, null);

        Empleado empleado = new Empleado();
        empleado.setSucursal(sucursal);
        empleado.setNombre(nombre.trim());
        empleado.setApellido(apellido.trim());
        empleado.setFechaNacimiento(fechaNacimiento);
        empleado.setTipoDocumento(tipoDocumento);
        empleado.setNumeroDocumento(numeroDocumento.trim());
        empleado.setTelefono(telefono);
        empleado.setCorreoElectronico(correoElectronico);
        empleado.setTipoEmpleado(tipoEmpleado);
        return empleadoRepository.save(empleado);
    }

    public void validar(String nombre, String apellido, LocalDate fechaNacimiento, TipoDocumento tipoDocumento,
        String numeroDocumento, String telefono, String correoElectronico, TipoEmpleado tipoEmpleado) {
        if (nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank()) {
            throw new BusinessException("Nombre y apellido son obligatorios");
        }
        if (fechaNacimiento == null) {
            throw new BusinessException("La fecha de nacimiento es obligatoria");
        }
        if (tipoDocumento == null || numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new BusinessException("El documento es obligatorio");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new BusinessException("El telefono es obligatorio");
        }
        if (correoElectronico == null || correoElectronico.isBlank()) {
            throw new BusinessException("El correo electronico es obligatorio");
        }
        if (tipoEmpleado == null) {
            throw new BusinessException("El tipo de empleado es obligatorio");
        }
    }

    private void validarDocumentoUnico(String numeroDocumento, String idActual) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new BusinessException("El documento es obligatorio");
        }
        String documentoNormalizado = numeroDocumento.trim().toLowerCase(Locale.ROOT);
        boolean existe = empleadoRepository.findAll().stream()
            .filter(e -> !e.isEliminado())
            .filter(e -> idActual == null || !e.getId().equals(idActual))
            .anyMatch(e -> e.getNumeroDocumento().trim().toLowerCase(Locale.ROOT).equals(documentoNormalizado));
        if (existe) {
            throw new BusinessException("Ya existe un empleado con ese documento");
        }
    }

    public Empleado modificarEmpleado(String idEmpleado, String nombre, String apellido, LocalDate fechaNacimiento,
        TipoDocumento tipoDocumento, String numeroDocumento, TipoEmpleado tipoEmpleado) {
        Empleado empleado = buscarEmpleado(idEmpleado);
        if (nombre != null && !nombre.isBlank()) {
            empleado.setNombre(nombre.trim());
        }
        if (apellido != null && !apellido.isBlank()) {
            empleado.setApellido(apellido.trim());
        }
        if (fechaNacimiento != null) {
            empleado.setFechaNacimiento(fechaNacimiento);
        }
        if (tipoDocumento != null) {
            empleado.setTipoDocumento(tipoDocumento);
        }
        if (numeroDocumento != null && !numeroDocumento.isBlank()
            && !empleado.getNumeroDocumento().equalsIgnoreCase(numeroDocumento.trim())) {
            validarDocumentoUnico(numeroDocumento, empleado.getId());
            empleado.setNumeroDocumento(numeroDocumento.trim());
        }
        if (tipoEmpleado != null) {
            empleado.setTipoEmpleado(tipoEmpleado);
        }
        return empleadoRepository.save(empleado);
    }

    @Transactional(readOnly = true)
    public List<Empleado> listarEmpleado() {
        return empleadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Empleado> listarEmpleadoActivo() {
        return empleadoRepository.findAll().stream()
            .filter(e -> !e.isEliminado())
            .toList();
    }

    public Empleado asociarEmpleadoUsuario(String idEmpleado, String idUsuario) {
        Empleado empleado = buscarEmpleado(idEmpleado);
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        empleado.setUsuario(usuario);
        usuario.setEmpleado(empleado);
        usuarioRepository.save(usuario);
        return empleadoRepository.save(empleado);
    }

    @Transactional(readOnly = true)
    public Empleado buscarEmpleado(String id) {
        return empleadoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Empleado no encontrado"));
    }
}

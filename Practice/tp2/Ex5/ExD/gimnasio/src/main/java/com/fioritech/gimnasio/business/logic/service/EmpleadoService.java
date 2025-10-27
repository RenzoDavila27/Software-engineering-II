package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Direccion;
import com.fioritech.gimnasio.business.domain.Empleado;
import com.fioritech.gimnasio.business.domain.Sucursal;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.TipoDocumento;
import com.fioritech.gimnasio.business.domain.enums.TipoEmpleado;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.EmpleadoRepository;
import com.fioritech.gimnasio.business.persistence.repository.SucursalRepository;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final SucursalService sucursalService;
    @Autowired
    private UsuarioService usuarioService;


     @Autowired
    private DireccionService direccionService;

    public EmpleadoService(EmpleadoRepository empleadoRepository, SucursalService sucursalService,
        SucursalRepository sucursalRepository) {
        this.empleadoRepository = empleadoRepository;
        this.sucursalService = sucursalService;
    }

    @Transactional
    public void crearEmpleado(String idSucursal, String nombre, String apellido, LocalDate fechaNacimiento,
        TipoDocumento tipoDocumento, String numeroDocumento, String telefono, String correoElectronico,
        TipoEmpleado tipoEmpleado,Usuario usuario,Direccion direccion)throws BusinessException {
        try{

            Usuario usuarioGuardado = usuarioService.crearUsuario(usuario.getNombreUsuario(), usuario.getClave(), usuario.getRol());
         
            Direccion direaccionGuardado = direccionService.crearDireccion(direccion.getCalle(),direccion.getNumeracion(),direccion.getBarrio(),direccion.getManzanaPiso(),direccion.getCasaDepartamento(),direccion.getReferencia(),direccion.getLocalidad().getId());
       
            Sucursal sucursal = sucursalService.buscarSucursal(idSucursal);
            
            validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico,
                tipoEmpleado, idSucursal, usuario, direccion);
            validarDocumentoUnico(numeroDocumento, null);

            Empleado empleado = new Empleado();
            empleado.setNombre(nombre.trim());
            empleado.setApellido(apellido.trim());
            empleado.setFechaNacimiento(fechaNacimiento);
            empleado.setTipoDocumento(tipoDocumento);
            empleado.setNumeroDocumento(numeroDocumento.trim());
            empleado.setTelefono(telefono);
            empleado.setCorreoElectronico(correoElectronico);
            empleado.setTipoEmpleado(tipoEmpleado);
            empleado.setSucursal(sucursal);
            empleado.setUsuario(usuarioGuardado);
            empleado.setDireccion(direaccionGuardado);
            empleadoRepository.save(empleado);

        }catch(BusinessException e){
            throw e;
        }catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("Error desconocido"); 
        }

    }

    public void validar(String nombre, String apellido, LocalDate fechaNacimiento, TipoDocumento tipoDocumento,
        String numeroDocumento, String telefono, String correoElectronico, TipoEmpleado tipoEmpleado, String idSucursal, Usuario usuario, Direccion direccion) {
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

         if (idSucursal == null) {
                throw new BusinessException("Debe indicar la sucursal");
            }
            
            if (usuario == null) {
                throw new BusinessException("Debe indicar el usuario");
            }
            
            if (direccion == null) {
                throw new BusinessException("Debe indicar la dieccion");
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

    @Transactional
    public void modificarEmpleado(String id, String nombre, String apellido, LocalDate fechaNacimiento, TipoDocumento tipoDocumento, String numeroDocumento, String telefono, String correoElectronico, TipoEmpleado tipoEmpleado, String idSucursal, Usuario usuario, Direccion direccion) throws BusinessException {
        try {

        	validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico, tipoEmpleado, idSucursal, usuario, direccion);;
            try{
            	Empleado empleadoExsitente = empleadoRepository.buscarEmpleadoPorNumeroDocumento(numeroDocumento);
                if (empleadoExsitente != null && !empleadoExsitente.getId().equals(id) && !empleadoExsitente.isEliminado()){
                  throw new BusinessException("Existe un empleado con el nombre indicado");  
                }
            } catch (NoResultException ex) {}
            Sucursal sucursal = sucursalService.buscarSucursal(idSucursal);
            Empleado empleadoModificado = buscarEmpleado(id);
            empleadoModificado.setNombre(nombre);
            empleadoModificado.setApellido(apellido);
            empleadoModificado.setFechaNacimiento(fechaNacimiento);
            empleadoModificado.setTipoDocumento(tipoDocumento);
            empleadoModificado.setNumeroDocumento(numeroDocumento);
            empleadoModificado.setTelefono(telefono);
            empleadoModificado.setCorreoElectronico(correoElectronico);
            empleadoModificado.setTipoEmpleado(tipoEmpleado);
            String nuevaClave = (usuario.getClave() != null && !usuario.getClave().isBlank()) ? usuario.getClave() : null;
            Usuario usuarioActualizado = usuarioService.modificarUsuario(
                usuario.getId(),
                usuario.getNombreUsuario(),
                nuevaClave,
                usuario.getRol()
            );
            empleadoModificado.setUsuario(usuarioActualizado);
            empleadoModificado.setSucursal(sucursal);
            empleadoModificado.setDireccion(direccion);
            empleadoModificado.setEliminado(false);
            empleadoRepository.save(empleadoModificado);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new BusinessException("Error de Sistemas");
        }
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

    //public Empleado asociarEmpleadoUsuario(String idEmpleado, String idUsuario) {
      //  Empleado empleado = buscarEmpleado(idEmpleado);
        //Usuario usuario = usuarioRepository.findById(idUsuario)
          //  .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        //empleado.setUsuario(usuario);
        //usuario.setEmpleado(empleado);
        //usuarioRepository.save(usuario);
        //return empleadoRepository.save(empleado);
    //}

    @Transactional(readOnly = true)
    public Empleado buscarEmpleado(String id) {
        return empleadoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Empleado no encontrado"));
    }

    @Transactional
    public void eliminarEmpleado(String id) {
        Empleado empleado = buscarEmpleado(id);
        empleado.setEliminado(true);
        empleadoRepository.save(empleado);
    }
}

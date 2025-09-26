package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Direccion;
import com.fioritech.gimnasio.business.domain.Empleado;
import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.Sucursal;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.EstadoCuotaMensual;
import com.fioritech.gimnasio.business.domain.enums.TipoDocumento;
import com.fioritech.gimnasio.business.domain.enums.TipoEmpleado;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.SocioRepository;
import com.fioritech.gimnasio.business.persistence.repository.UsuarioRepository;

import java.util.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SocioService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DireccionService direccionService;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private SucursalService sucursalService;
    
    @Autowired
    private CuotaMensualService cuotaMensualService;

    @Transactional
    public void crearSocio(String idSucursal, String nombre, String apellido, LocalDate fechaNacimiento,
        TipoDocumento tipoDocumento, String numeroDocumento, String telefono, String correoElectronico,
        Usuario usuario,Direccion direccion) throws BusinessException {
        try{

            Usuario usuarioGuardado = usuarioService.crearUsuario(usuario.getNombreUsuario(), usuario.getClave(), usuario.getRol());
         
            Direccion direaccionGuardado = direccionService.crearDireccion(direccion.getCalle(),direccion.getNumeracion(),direccion.getBarrio(),direccion.getManzanaPiso(),direccion.getCasaDepartamento(),direccion.getReferencia(),direccion.getLocalidad().getId());
       
            Sucursal sucursal = sucursalService.buscarSucursal(idSucursal);

            Long numeroSocio = socioRepository.obtenerProximoNumeroSocio() +1;
            
            validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico,numeroSocio);
            validarDocumentoUnico(numeroDocumento, null);

            Socio socio = new Socio();
            socio.setNombre(nombre.trim());
            socio.setApellido(apellido.trim());
            socio.setFechaNacimiento(fechaNacimiento);
            socio.setTipoDocumento(tipoDocumento);
            socio.setNumeroDocumento(numeroDocumento.trim());
            socio.setTelefono(telefono);
            socio.setCorreoElectronico(correoElectronico);
            socio.setNumeroSocio(numeroSocio);
            socio.setSucursal(sucursal);
            socio.setUsuario(usuarioGuardado);
            socio.setDireccion(direaccionGuardado);
            socioRepository.save(socio);
            cuotaMensualService.generarPrimeraCuotaParaNuevoSocio(socio);

        }catch(BusinessException e){
            throw e;
        }catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("Error desconocido"); 
        }

    }


    public void validar(String nombre, String apellido, LocalDate fechaNacimiento, TipoDocumento tipoDocumento,
        String numeroDocumento, String telefono, String correoElectronico, Long numeroSocio) {
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
        if (numeroSocio == null) {
            throw new BusinessException("El numero de socio es obligatorio");
        }
    }

    private void validarDocumentoUnico(String numeroDocumento, String idActual) {
        String documentoNormalizado = numeroDocumento.trim().toLowerCase(Locale.ROOT);
        boolean existe = socioRepository.findAll().stream()
            .filter(s -> !s.isEliminado())
            .filter(s -> idActual == null || !s.getId().equals(idActual))
            .anyMatch(s -> s.getNumeroDocumento().trim().toLowerCase(Locale.ROOT).equals(documentoNormalizado));
        if (existe) {
            throw new BusinessException("Ya existe un socio con ese documento");
        }
    }

    private void validarNumeroSocioUnico(Long numeroSocio, String idActual) {
        boolean existe = socioRepository.findAll().stream()
            .filter(s -> !s.isEliminado())
            .filter(s -> idActual == null || !s.getId().equals(idActual))
            .anyMatch(s -> s.getNumeroSocio().equals(numeroSocio));
        if (existe) {
            throw new BusinessException("Ya existe un socio con ese numero");
        }
    }

    public Socio modificarSocio(String idSocio, String nombre, String apellido, LocalDate fechaNacimiento,
        TipoDocumento tipoDocumento, String numeroDocumento, String telefono, String correoElectronico, Long numeroSocio) {
        Socio socio = buscarSocio(idSocio);
        if (nombre != null && !nombre.isBlank()) {
            socio.setNombre(nombre.trim());
        }
        if (apellido != null && !apellido.isBlank()) {
            socio.setApellido(apellido.trim());
        }
        if (fechaNacimiento != null) {
            socio.setFechaNacimiento(fechaNacimiento);
        }
        if (tipoDocumento != null) {
            socio.setTipoDocumento(tipoDocumento);
        }
        if(telefono != null){
            socio.setTelefono(telefono);
        }
        if(correoElectronico != null){
            socio.setCorreoElectronico(correoElectronico);
        }
        if (numeroDocumento != null && !numeroDocumento.isBlank()
            && !socio.getNumeroDocumento().equalsIgnoreCase(numeroDocumento.trim())) {
            validarDocumentoUnico(numeroDocumento, socio.getId());
            socio.setNumeroDocumento(numeroDocumento.trim());
        }
        if (numeroSocio != null && !numeroSocio.equals(socio.getNumeroSocio())) {
            validarNumeroSocioUnico(numeroSocio, socio.getId());
            socio.setNumeroSocio(numeroSocio);
        }
        return socioRepository.save(socio);
    }

    @Transactional(readOnly = true)
    public List<Socio> listarSocio() {
        return socioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Socio> listarSocioActivo() {
        return socioRepository.findAll().stream()
            .filter(s -> !s.isEliminado())
            .toList();
    }

    @Transactional(readOnly = true)
    public Socio buscarSocio(String id) {
        return socioRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Socio no encontrado"));
    }

    @Transactional(readOnly = true)
    public Socio buscarSocioPorUsuario(String idUsuario) {
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new BusinessException("El usuario es obligatorio");
        }
        Optional<Socio> socio = socioRepository.findByUsuarioIdAndEliminadoFalse(idUsuario);
        return socio.orElseThrow(() -> new BusinessException("No se encontró un socio asociado al usuario"));
    }

    @Transactional
    public void eliminarSocio(String id) throws BusinessException{

        try {
            Socio socio = buscarSocio(id);
            socio.setEliminado(true);
            socioRepository.save(socio);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("Error de sistema");
        }
    }

    public  Collection<Socio> listarCumpleanieros(){
        try{
            int dia = LocalDate.now().getDayOfMonth();
            int mes = LocalDate.now().getMonthValue();
            Collection<Socio> socio = socioRepository.listarCumpleanieros(dia,mes);
            if(socio.isEmpty()){
                throw new BusinessException("No hay socios que cumplan años hoy");
            }
            return socio;
        }catch(BusinessException e){
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Collection<Socio> listarSociosActivos() {
        Collection<Socio> socio = socioRepository.listarSociosActivos();
        return socio;
        
    }

    public Collection<Socio> SocioConDeudas(EstadoCuotaMensual estado){

            Collection<Socio> socio = socioRepository.SocioConDeudas(estado);
            return socio;
    }  
 }

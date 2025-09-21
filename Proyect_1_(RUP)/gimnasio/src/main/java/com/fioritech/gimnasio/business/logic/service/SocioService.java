package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.TipoDocumento;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.SocioRepository;
import com.fioritech.gimnasio.business.persistence.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SocioService {

    private final SocioRepository socioRepository;
    private final UsuarioRepository usuarioRepository;

    public SocioService(SocioRepository socioRepository, UsuarioRepository usuarioRepository) {
        this.socioRepository = socioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Socio crearSocio(String nombre, String apellido, LocalDate fechaNacimiento, TipoDocumento tipoDocumento,
        String numeroDocumento, String telefono, String correoElectronico, Long numeroSocio) {
        validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico,
            numeroSocio);
        validarDocumentoUnico(numeroDocumento, null);
        validarNumeroSocioUnico(numeroSocio, null);

        Socio socio = new Socio();
        socio.setNombre(nombre.trim());
        socio.setApellido(apellido.trim());
        socio.setFechaNacimiento(fechaNacimiento);
        socio.setTipoDocumento(tipoDocumento);
        socio.setNumeroDocumento(numeroDocumento.trim());
        socio.setTelefono(telefono);
        socio.setCorreoElectronico(correoElectronico);
        socio.setNumeroSocio(numeroSocio);
        return socioRepository.save(socio);
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

    public Socio asociarSocioUsuario(String idSocio, String idUsuario) {
        Socio socio = buscarSocio(idSocio);
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        socio.setUsuario(usuario);
        usuario.setSocio(socio);
        usuarioRepository.save(usuario);
        return socioRepository.save(socio);
    }

    @Transactional(readOnly = true)
    public Socio buscarSocio(String id) {
        return socioRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Socio no encontrado"));
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
}

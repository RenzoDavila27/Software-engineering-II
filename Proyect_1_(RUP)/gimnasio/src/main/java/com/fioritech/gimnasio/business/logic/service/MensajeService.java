package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Mensaje;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.TipoMensaje;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.MensajeRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioService usuarioService;

    public MensajeService(MensajeRepository mensajeRepository, UsuarioService usuarioService) {
        this.mensajeRepository = mensajeRepository;
        this.usuarioService = usuarioService;
    }

    public Mensaje crearMensaje(String idUsuario, String titulo, String texto, TipoMensaje tipoMensaje) {
        validar(idUsuario, titulo, texto, tipoMensaje);
        Usuario usuario = usuarioService.buscarUsuario(idUsuario);
        Mensaje mensaje = new Mensaje();
        mensaje.setUsuario(usuario);
        mensaje.setTitulo(titulo.trim());
        mensaje.setTexto(texto.trim());
        mensaje.setTipoMensaje(tipoMensaje);
        return mensajeRepository.save(mensaje);
    }

    public void validar(String idUsuario, String titulo, String texto, TipoMensaje tipoMensaje) {
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new BusinessException("El usuario es obligatorio");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new BusinessException("El titulo es obligatorio");
        }
        if (texto == null || texto.isBlank()) {
            throw new BusinessException("El texto es obligatorio");
        }
        if (tipoMensaje == null) {
            throw new BusinessException("El tipo de mensaje es obligatorio");
        }
    }

    public Mensaje modificarMensaje(String id, String idUsuario, String titulo, String texto, TipoMensaje tipoMensaje) {
        Mensaje mensaje = buscarMensaje(id);
        if (idUsuario != null && !idUsuario.isBlank()) {
            mensaje.setUsuario(usuarioService.buscarUsuario(idUsuario));
        }
        if (titulo != null && !titulo.isBlank()) {
            mensaje.setTitulo(titulo.trim());
        }
        if (texto != null && !texto.isBlank()) {
            mensaje.setTexto(texto.trim());
        }
        if (tipoMensaje != null) {
            mensaje.setTipoMensaje(tipoMensaje);
        }
        return mensajeRepository.save(mensaje);
    }

    public void eliminarMensaje(String id) {
        Mensaje mensaje = buscarMensaje(id);
        mensaje.setEliminado(true);
        mensajeRepository.save(mensaje);
    }

    @Transactional(readOnly = true)
    public Mensaje buscarMensaje(String id) {
        return mensajeRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Mensaje no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Mensaje> listarMensaje() {
        return mensajeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Mensaje> listarMensajeActivo() {
        return mensajeRepository.findAll().stream()
            .filter(m -> !m.isEliminado())
            .toList();
    }

    public Mensaje enviarMensaje(String id) {
        Mensaje mensaje = buscarMensaje(id);
        mensaje.setTexto(mensaje.getTexto() + "\nEnviado: " + LocalDateTime.now());
        return mensajeRepository.save(mensaje);
    }
}

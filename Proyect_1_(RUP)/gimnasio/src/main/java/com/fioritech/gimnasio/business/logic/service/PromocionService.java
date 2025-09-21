package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Promocion;
import com.fioritech.gimnasio.business.domain.Provincia;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.PromocionRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PromocionService {

    private final PromocionRepository promocionRepository;
    private final UsuarioService usuarioService;

    public PromocionService(PromocionRepository promocionRepository, UsuarioService usuarioService) {
        this.promocionRepository = promocionRepository;
        this.usuarioService = usuarioService;
    }

    public Promocion crearPromocion(String idUsuario, LocalDate fechaPromocion, String titulo, String texto) {
        validar(idUsuario, fechaPromocion, titulo, texto);
        Usuario usuario = usuarioService.buscarUsuario(idUsuario);
        Promocion promocion = new Promocion();
        promocion.setUsuario(usuario);
        promocion.setFechaEnvioPromocion(fechaPromocion);
        promocion.setTitulo(titulo.trim());
        promocion.setTexto(texto.trim());
        promocion.setCantidadSociosEnviados(0L);
        return promocionRepository.save(promocion);
    }

    public void validar(String idUsuario, LocalDate fechaPromocion, String titulo, String texto) {
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new BusinessException("El usuario es obligatorio");
        }
        if (fechaPromocion == null) {
            throw new BusinessException("La fecha de envio es obligatoria");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new BusinessException("El titulo es obligatorio");
        }
        if (texto == null || texto.isBlank()) {
            throw new BusinessException("El texto es obligatorio");
        }
    }

    @Transactional(readOnly = true)
    public Promocion buscarPromocion(String id) {
        return promocionRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Promocion no encontrada"));
    }

    public Promocion modificarPromocion(String id, String idUsuario, LocalDate fechaPromocion, String titulo,
        String texto) {
        Promocion promocion = buscarPromocion(id);
        if (idUsuario != null && !idUsuario.isBlank()) {
            promocion.setUsuario(usuarioService.buscarUsuario(idUsuario));
        }
        if (fechaPromocion != null) {
            promocion.setFechaEnvioPromocion(fechaPromocion);
        }
        if (titulo != null && !titulo.isBlank()) {
            promocion.setTitulo(titulo.trim());
        }
        if (texto != null && !texto.isBlank()) {
            promocion.setTexto(texto.trim());
        }
        return promocionRepository.save(promocion);
    }

    @Transactional
    public void eliminarPromocion(String id) {
        Promocion promocion= buscarPromocion(id);
        promocion.setEliminado(true);
        promocionRepository.save(promocion);
    }

    @Transactional(readOnly = true)
    public List<Promocion> listarPromocion() {
        return promocionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Promocion> listarPromocionActivo() {
        return promocionRepository.findAll().stream()
            .filter(p -> !p.isEliminado())
            .toList();
    }
}

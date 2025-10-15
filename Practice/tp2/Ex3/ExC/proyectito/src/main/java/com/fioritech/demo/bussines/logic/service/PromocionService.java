package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Promocion;
import com.fioritech.demo.bussines.domain.PromocionTipo;
import com.fioritech.demo.bussines.domain.Proveedor;
import com.fioritech.demo.bussines.domain.Usuario;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.template.CrudTemplateService;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.PromocionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PromocionService extends CrudTemplateService<Promocion, Long> {

    private static final String PROMOCION_EMAIL_BUTTON_TEXT = "Ir a UNCUYO";
    private static final String PROMOCION_EMAIL_LINK = "https://www.uncuyo.edu.ar/";
    private static final String PROMOCION_EMAIL_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Email</title>
            </head>
            <body style="margin:0; padding:0; font-family: Arial, Helvetica, sans-serif; background-color:#f4f4f4;">
                <table align="center" width="600" style="border-collapse: collapse; background-color:#ffffff; border-radius:10px; overflow:hidden;">
                    <tr>
                        <td style="background-color:#4CAF50; padding:20px; text-align:center;">
                            <h1 style="color:#ffffff; margin:0;">%s</h1>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding:30px; text-align:center; color:#333;">
                            <p style="font-size:16px; line-height:1.6; margin-bottom:20px;">
                                %s
                            </p>
                            <a href="%s"
                               style="display:inline-block; padding:12px 24px; background-color:#4CAF50; color:#fff; text-decoration:none; font-weight:bold; border-radius:5px; font-size:16px;">
                                %s
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <td style="background-color:#f4f4f4; padding:15px; text-align:center; font-size:12px; color:#777;">
                            Copyright 2025 - Fioritech. Todos los derechos reservados.
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """;

    private final PromocionRepository promocionRepository;
    private final EmailService emailService;
    private final UsuarioService usuarioService;
    private final ProveedorService proveedorService;

    public PromocionService(PromocionRepository promocionRepository,
                            EmailService emailService,
                            UsuarioService usuarioService,
                            ProveedorService proveedorService) {
        this.promocionRepository = promocionRepository;
        this.emailService = emailService;
        this.usuarioService = usuarioService;
        this.proveedorService = proveedorService;
    }

    public Collection<Promocion> listarPromociones() {
        return listarEntidades();
    }

    public Collection<Promocion> listarPromocionesPorTipo(PromocionTipo tipo) {
        return promocionRepository.buscarActivasPorTipo(tipo);
    }

    public Optional<Promocion> obtenerPromocionPorTipo(PromocionTipo tipo) {
        return promocionRepository.findFirstByTipoAndEliminadoFalse(tipo);
    }

    public Promocion crearPromocion(Promocion promocion) {
        if (promocion.getTipo() == null) {
            promocion.setTipo(PromocionTipo.PROMOCION_GENERAL);
        }
        return crearEntidad(promocion);
    }

    public Promocion modificarPromocion(Long id, Promocion cambios) {
        Promocion existente = buscarEntidad(id);
        if (cambios.getTipo() == null) {
            cambios.setTipo(existente.getTipo());
        }
        if (existente.getTipo() != cambios.getTipo()) {
            throw new BusinessException("No se puede cambiar el tipo de la promocion");
        }
        return modificarEntidad(id, cambios);
    }

    public void eliminarPromocion(Long id) {
        eliminarEntidad(id);
    }

    public Promocion buscarPromocionPorId(Long id) {
        return buscarEntidad(id);
    }

    public void enviarPromocion(Long id) {
        Promocion promocion = buscarEntidad(id);
        int enviados = enviarPromocionSegunTipo(promocion);
        if (enviados == 0) {
            throw new BusinessException("No hay destinatarios con correo valido para enviar la promocion");
        }
    }

    public Optional<Promocion> obtenerPromocionPorTitulo(String titulo) {
        if (ValidationUtils.isBlank(titulo)) {
            return Optional.empty();
        }
        return promocionRepository.findByTituloIgnoreCase(titulo.trim());
    }

    public void enviarPromocionProgramada(Promocion promocion) {
        int enviados = enviarPromocionSegunTipo(promocion);
        if (enviados == 0) {
            throw new BusinessException("No hay destinatarios con correo valido para enviar la promocion");
        }
    }

    @Override
    protected void validarEntidad(Promocion promocion) {
        if (promocion == null) {
            throw new BusinessException("La promocion es obligatoria");
        }
        if (ValidationUtils.isBlank(promocion.getTitulo())) {
            throw new BusinessException("El titulo de la promocion es obligatorio");
        }
        if (ValidationUtils.isBlank(promocion.getContenido())) {
            throw new BusinessException("El contenido de la promocion es obligatorio");
        }
        if (promocion.getTipo() == null) {
            throw new BusinessException("El tipo de la promocion es obligatorio");
        }
    }

    @Override
    protected void validarEntidadNueva(Promocion promocion) {
        if (promocion.getId() != null) {
            throw new BusinessException("La promocion ya tiene un id asignado");
        }
    }

    @Override
    protected void antesDeCrear(Promocion promocion) {
        desactivarPromocionesVigentes(promocion.getTipo());
        normalizarPromocion(promocion);
        promocion.setEliminado(false);
    }

    @Override
    protected void aplicarCambios(Promocion existente, Promocion cambios) {
        normalizarPromocion(cambios);
        existente.setTitulo(cambios.getTitulo());
        existente.setContenido(cambios.getContenido());
    }

    @Override
    protected void marcarEliminado(Promocion promocion) {
        promocion.setEliminado(true);
    }

    @Override
    protected Promocion guardar(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    @Override
    protected Promocion obtenerPorId(Long id) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe la promocion con id " + id));
        if (promocion.isEliminado()) {
            throw new BusinessException("La promocion con id " + id + " esta eliminada");
        }
        return promocion;
    }

    @Override
    protected Collection<Promocion> obtenerListado() {
        return promocionRepository.buscarPromocionesActivas();
    }

    private void desactivarPromocionesVigentes(PromocionTipo tipo) {
        Collection<Promocion> activas = promocionRepository.buscarActivasPorTipo(tipo);
        if (activas.isEmpty()) {
            return;
        }
        activas.forEach(promocion -> promocion.setEliminado(true));
        promocionRepository.saveAll(activas);
    }

    private void normalizarPromocion(Promocion promocion) {
        promocion.setTitulo(promocion.getTitulo().trim());
        promocion.setContenido(promocion.getContenido().trim());
    }

    private int enviarPromocionSegunTipo(Promocion promocion) {
        if (promocion.getTipo() == PromocionTipo.SALUDO_FIN_ANIO) {
            return enviarCorreosAProveedores(promocion);
        }
        return enviarCorreosAUsuarios(promocion);
    }

    private int enviarCorreosAUsuarios(Promocion promocion) {
        Collection<Usuario> usuarios = usuarioService.listarUsuarios();
        Set<String> correos = new LinkedHashSet<>();
        for (Usuario usuario : usuarios) {
            if (ValidationUtils.isBlank(usuario.getCorreo())) {
                continue;
            }
            correos.add(usuario.getCorreo().trim());
        }
        return enviarCorreoPromocional(promocion, correos);
    }

    private int enviarCorreosAProveedores(Promocion promocion) {
        Collection<Proveedor> proveedores = proveedorService.listarProveedores();
        Set<String> correos = new LinkedHashSet<>();
        for (Proveedor proveedor : proveedores) {
            if (ValidationUtils.isBlank(proveedor.getCorreo())) {
                continue;
            }
            correos.add(proveedor.getCorreo().trim());
        }
        return enviarCorreoPromocional(promocion, correos);
    }

    private int enviarCorreoPromocional(Promocion promocion, Set<String> destinatarios) {
        if (destinatarios.isEmpty()) {
            return 0;
        }

        String contenido = PROMOCION_EMAIL_TEMPLATE.formatted(
                promocion.getTitulo(),
                promocion.getContenido(),
                PROMOCION_EMAIL_LINK,
                PROMOCION_EMAIL_BUTTON_TEXT);

        int exitosos = 0;
        for (String correo : destinatarios) {
            boolean enviado = emailService.enviarCorreoHtml(
                    correo,
                    promocion.getTitulo(),
                    contenido);
            if (enviado) {
                exitosos++;
            }
        }
        return exitosos;
    }
}


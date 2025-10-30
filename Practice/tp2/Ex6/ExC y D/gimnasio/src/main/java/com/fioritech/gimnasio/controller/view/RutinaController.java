package com.fioritech.gimnasio.controller.view;

import com.fioritech.gimnasio.business.domain.Empleado;
import com.fioritech.gimnasio.business.domain.Rutina;
import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.EstadoRutina;
import com.fioritech.gimnasio.business.domain.enums.RolUsuario;
import com.fioritech.gimnasio.business.domain.enums.TipoEmpleado;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.EmpleadoService;
import com.fioritech.gimnasio.business.logic.service.RutinaService;
import com.fioritech.gimnasio.business.logic.service.SocioService;
import com.fioritech.gimnasio.business.domain.DetalleRutina;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RutinaController {

    private final RutinaService rutinaService;
    private final EmpleadoService empleadoService;
    private final SocioService socioService;

    public RutinaController(RutinaService rutinaService, EmpleadoService empleadoService,
        SocioService socioService) {
        this.rutinaService = rutinaService;
        this.empleadoService = empleadoService;
        this.socioService = socioService;
    }

    private void cargarCatalogos(Model model) {
        List<Empleado> profesores = empleadoService.listarEmpleadoActivo().stream()
            .filter(e -> e.getTipoEmpleado() == TipoEmpleado.PROFESOR)
            .collect(Collectors.toList());
        model.addAttribute("profesores", profesores);
        model.addAttribute("socios", socioService.listarSocioActivo());
        model.addAttribute("estadosRutina", EstadoRutina.values());
    }

    private boolean esSocio(HttpSession session) {
        if (session == null) {
            return false;
        }
        Object rol = session.getAttribute("rol");
        return RolUsuario.SOCIO.name().equals(rol);
    }

    private Socio obtenerSocioSesion(HttpSession session) {
        if (session == null) {
            throw new BusinessException("La sesión no es válida");
        }
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
        if (usuario == null || usuario.getId() == null || usuario.getId().isBlank()) {
            throw new BusinessException("No se encontró un usuario asociado a la sesión");
        }
        return socioService.buscarSocioPorUsuario(usuario.getId());
    }

    private List<DetalleRutina> obtenerDetalles(Rutina rutina) {
        if (rutina == null || rutina.getDetalles() == null) {
            return Collections.emptyList();
        }
        return rutina.getDetalles();
    }

    @GetMapping("/rutina/listaRutina")
    public String listaRutina(Model model, HttpSession session) {
        try {
            List<Rutina> listaRutina;
            if (esSocio(session)) {
                Socio socio = obtenerSocioSesion(session);
                listaRutina = rutinaService.listarRutinaActivoPorSocio(socio.getId());
            } else {
                listaRutina = rutinaService.listarRutinaActivo();
            }
            model.addAttribute("listaRutina", listaRutina);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
        }
        return "view/rutina/lRutina";
    }

    @GetMapping("/rutina/altaRutina")
    public String alta(Rutina rutina, Model model) {
        model.addAttribute("isDisabled", false);
        model.addAttribute("detallesRutina", obtenerDetalles(rutina));
        cargarCatalogos(model);
        return "view/rutina/eRutina";
    }

    @GetMapping("/rutina/consultar/{id}")
    public String consultar(@PathVariable("id") String idRutina, Model model, RedirectAttributes attributes,
        HttpSession session) {
        try {
            Rutina rutina = rutinaService.buscarRutina(idRutina);
            if (esSocio(session)) {
                Socio socio = obtenerSocioSesion(session);
                if (!rutina.getSocio().getId().equals(socio.getId())) {
                    attributes.addFlashAttribute("msgError", "No está autorizado a consultar la rutina indicada.");
                    return "redirect:/rutina/listaRutina";
                }
            }
            model.addAttribute("rutina", rutina);
            model.addAttribute("isDisabled", true);
            model.addAttribute("detallesRutina", obtenerDetalles(rutina));
            cargarCatalogos(model);
            return "view/rutina/eRutina";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/rutina/listaRutina";
        }
    }

    @GetMapping("/rutina/modificar/{id}")
    public String modificar(@PathVariable("id") String idRutina, Model model, RedirectAttributes attributes) {
        try {
            Rutina rutina = rutinaService.buscarRutina(idRutina);
            model.addAttribute("rutina", rutina);
            model.addAttribute("isDisabled", false);
            model.addAttribute("detallesRutina", obtenerDetalles(rutina));
            cargarCatalogos(model);
            return "view/rutina/eRutina";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/rutina/listaRutina";
        }
    }

    @GetMapping("/rutina/baja/{id}")
    public String baja(@PathVariable("id") String idRutina, RedirectAttributes attributes) {
        try {
            rutinaService.eliminarRutina(idRutina);
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
        }
        return "redirect:/rutina/listaRutina";
    }

    @PostMapping("/rutina/aceptarEditRutina")
    public String aceptarEdit(Rutina rutina, BindingResult result, RedirectAttributes attributes, Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("msgError", "Error de Sistema");
                cargarCatalogos(model);
                model.addAttribute("detallesRutina", obtenerDetalles(rutina));
                model.addAttribute("isDisabled", false);
                return "view/rutina/eRutina";
            }

            if (rutina.getProfesor() == null || rutina.getProfesor().getId() == null
                || rutina.getProfesor().getId().trim().isEmpty()) {
                model.addAttribute("msgError", "Debe seleccionar un profesor");
                cargarCatalogos(model);
                model.addAttribute("detallesRutina", obtenerDetalles(rutina));
                model.addAttribute("isDisabled", false);
                return "view/rutina/eRutina";
            }

            if (rutina.getSocio() == null || rutina.getSocio().getId() == null
                || rutina.getSocio().getId().trim().isEmpty()) {
                model.addAttribute("msgError", "Debe seleccionar un socio");
                cargarCatalogos(model);
                model.addAttribute("detallesRutina", obtenerDetalles(rutina));
                model.addAttribute("isDisabled", false);
                return "view/rutina/eRutina";
            }

            if (rutina.getId() == null || rutina.getId().trim().isEmpty()) {
                rutinaService.crearRutina(
                    rutina.getProfesor().getId(),
                    rutina.getSocio().getId(),
                    rutina.getFechaInicio(),
                    rutina.getFechaFinalizacion(),
                    rutina.getDetalles()
                );
            } else {
                Rutina existente = rutinaService.buscarRutina(rutina.getId());
                rutinaService.modificarRutina(
                    rutina.getId(),
                    rutina.getProfesor() != null ? rutina.getProfesor().getId() : null,
                    rutina.getSocio() != null ? rutina.getSocio().getId() : null,
                    rutina.getFechaInicio(),
                    rutina.getFechaFinalizacion(),
                    rutina.getDetalles()
                );
                if (rutina.getEstadoRutina() != null
                    && rutina.getEstadoRutina() != existente.getEstadoRutina()) {
                    rutinaService.modificarEstadoRutina(rutina.getId(), rutina.getEstadoRutina());
                }
            }

            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/rutina/listaRutina";

        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            cargarCatalogos(model);
            model.addAttribute("detallesRutina", obtenerDetalles(rutina));
            model.addAttribute("isDisabled", false);
            return "view/rutina/eRutina";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            cargarCatalogos(model);
            model.addAttribute("detallesRutina", obtenerDetalles(rutina));
            model.addAttribute("isDisabled", false);
            return "view/rutina/eRutina";
        }
    }

    @GetMapping("/rutina/cancelarEditRutina")
    public String cancelarEdit() {
        return "redirect:/rutina/listaRutina";
    }
}

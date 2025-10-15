package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.controller.template.CrudTemplateController;
import com.fioritech.demo.bussines.domain.Promocion;
import com.fioritech.demo.bussines.domain.PromocionTipo;
import com.fioritech.demo.bussines.logic.service.PromocionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;

@Controller
@RequestMapping("/promocion")
public class PromocionController extends CrudTemplateController<Promocion, Long> {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @Override
    protected Collection<Promocion> listarEntidades() {
        return promocionService.listarPromociones();
    }

    @Override
    protected void crearEntidad(Promocion promocion) {
        promocionService.crearPromocion(promocion);
    }

    @Override
    protected Promocion buscarEntidad(Long id) {
        return promocionService.buscarPromocionPorId(id);
    }

    @Override
    protected void modificarEntidad(Long id, Promocion cambios) {
        promocionService.modificarPromocion(id, cambios);
    }

    @Override
    protected void eliminarEntidad(Long id) {
        promocionService.eliminarPromocion(id);
    }

    @Override
    protected Promocion crearInstanciaFormulario() {
        return new Promocion();
    }

    @Override
    protected void prepararInstanciaNueva(Promocion promocion) {
        PromocionTipo tipoSolicitado = obtenerTipoDesdeRequest();
        if (tipoSolicitado != null) {
            promocion.setTipo(tipoSolicitado);
        } else if (promocion.getTipo() == null) {
            promocion.setTipo(PromocionTipo.PROMOCION_GENERAL);
        }
    }

    @Override
    protected void prepararInstanciaExistente(Promocion promocion) {
        if (promocion.getTipo() == null) {
            promocion.setTipo(PromocionTipo.PROMOCION_GENERAL);
        }
    }

    @Override
    protected void prepararModeloFormulario(Model model) {
        PromocionTipo tipo = obtenerTipoDesdeRequest();
        if (tipo != null) {
            model.addAttribute("tipo", tipo);
            return;
        }
        Object promocion = model.getAttribute(obtenerNombreModeloFormulario());
        if (promocion instanceof Promocion promocionSeleccionada && promocionSeleccionada.getTipo() != null) {
            model.addAttribute("tipo", promocionSeleccionada.getTipo());
        }
    }

    @Override
    protected void prepararModeloListado(Model model) {
        Promocion promocionGeneral = promocionService.listarPromocionesPorTipo(PromocionTipo.PROMOCION_GENERAL)
                .stream()
                .findFirst()
                .orElse(null);
        Promocion saludoFinAnio = promocionService.listarPromocionesPorTipo(PromocionTipo.SALUDO_FIN_ANIO)
                .stream()
                .findFirst()
                .orElse(null);
        model.addAttribute("promocionGeneral", promocionGeneral);
        model.addAttribute("saludoFinAnio", saludoFinAnio);
    }

    @Override
    protected String obtenerNombreModeloListado() {
        return "promociones";
    }

    @Override
    protected String obtenerNombreModeloFormulario() {
        return "promocion";
    }

    @Override
    protected String obtenerVistaListado() {
        return "promocion/listar";
    }

    @Override
    protected String obtenerVistaCreacion() {
        return "promocion/crear";
    }

    @Override
    protected String obtenerVistaEdicion() {
        return "promocion/modificar";
    }

    @Override
    protected String obtenerRedirectListado() {
        return "redirect:/promocion/listar";
    }

    @GetMapping("/modificar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        model.addAttribute("tipo", promocionService.buscarPromocionPorId(id).getTipo());
        return super.mostrarFormularioEdicion(id, model);
    }

    private PromocionTipo obtenerTipoDesdeRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        String tipoParam = attributes.getRequest().getParameter("tipo");
        if (tipoParam == null || tipoParam.isBlank()) {
            return null;
        }
        try {
            return PromocionTipo.valueOf(tipoParam);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}

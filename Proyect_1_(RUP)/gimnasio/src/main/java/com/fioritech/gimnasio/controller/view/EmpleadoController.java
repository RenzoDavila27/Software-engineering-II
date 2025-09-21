package com.fioritech.gimnasio.controller.view;

import com.fioritech.gimnasio.business.domain.Empleado;
import com.fioritech.gimnasio.business.domain.enums.TipoDocumento;
import com.fioritech.gimnasio.business.domain.enums.TipoEmpleado;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.EmpleadoService;
import com.fioritech.gimnasio.business.logic.service.SucursalService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final SucursalService sucursalService;

    public EmpleadoController(EmpleadoService empleadoService, SucursalService sucursalService) {
        this.empleadoService = empleadoService;
        this.sucursalService = sucursalService;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("tiposEmpleado", TipoEmpleado.values());
        model.addAttribute("sucursales", sucursalService.listarSucursalActiva());
    }

    @GetMapping("/empleado/listaEmpleado")
    public String listaEmpleado(Model model) {
        try {
            List<Empleado> listaEmpleado = empleadoService.listarEmpleadoActivo();
            model.addAttribute("listaEmpleado", listaEmpleado);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
        }
        return "view/empleado/lEmpleado";
    }

    @GetMapping("/empleado/altaEmpleado")
    public String alta(Empleado empleado, Model model) {
        model.addAttribute("isDisabled", false);
        cargarCatalogos(model);
        return "view/empleado/eEmpleado";
    }

    @GetMapping("/empleado/consultar/{id}")
    public String consultar(@PathVariable("id") String idEmpleado, Model model, RedirectAttributes attributes) {
        try {
            Empleado empleado = empleadoService.buscarEmpleado(idEmpleado);
            model.addAttribute("empleado", empleado);
            model.addAttribute("isDisabled", true);
            cargarCatalogos(model);
            return "view/empleado/eEmpleado";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/empleado/listaEmpleado";
        }
    }

    @GetMapping("/empleado/modificar/{id}")
    public String modificar(@PathVariable("id") String idEmpleado, Model model, RedirectAttributes attributes) {
        try {
            Empleado empleado = empleadoService.buscarEmpleado(idEmpleado);
            model.addAttribute("empleado", empleado);
            model.addAttribute("isDisabled", false);
            cargarCatalogos(model);
            return "view/empleado/eEmpleado";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/empleado/listaEmpleado";
        }
    }

    @GetMapping("/empleado/baja/{id}")
    public String baja(@PathVariable("id") String idEmpleado, RedirectAttributes attributes) {
        try {
            empleadoService.eliminarEmpleado(idEmpleado);
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
        }
        return "redirect:/empleado/listaEmpleado";
    }

    @PostMapping("/empleado/aceptarEditEmpleado")
    public String aceptarEdit(Empleado empleado, BindingResult result, RedirectAttributes attributes, Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("msgError", "Error de Sistema");
                cargarCatalogos(model);
                return "view/empleado/eEmpleado";
            }

            if (empleado.getSucursal() == null || empleado.getSucursal().getId() == null
                || empleado.getSucursal().getId().trim().isEmpty()) {
                model.addAttribute("msgError", "Debe seleccionar una sucursal");
                cargarCatalogos(model);
                return "view/empleado/eEmpleado";
            }

            if (empleado.getId() == null || empleado.getId().trim().isEmpty()) {
                empleadoService.crearEmpleado(
                    empleado.getSucursal() != null ? empleado.getSucursal().getId() : null,
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getFechaNacimiento(),
                    empleado.getTipoDocumento(),
                    empleado.getNumeroDocumento(),
                    empleado.getTelefono(),
                    empleado.getCorreoElectronico(),
                    empleado.getTipoEmpleado()
                );
            } else {
                empleadoService.modificarEmpleado(
                    empleado.getId(),
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getFechaNacimiento(),
                    empleado.getTipoDocumento(),
                    empleado.getNumeroDocumento(),
                    empleado.getTipoEmpleado()
                );
            }

            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/empleado/listaEmpleado";

        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            cargarCatalogos(model);
            return "view/empleado/eEmpleado";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            cargarCatalogos(model);
            return "view/empleado/eEmpleado";
        }
    }

    @GetMapping("/empleado/cancelarEditEmpleado")
    public String cancelarEdit() {
        return "redirect:/empleado/listaEmpleado";
    }
}

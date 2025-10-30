package com.example.mecanic.controller;

import com.example.mecanic.bussines.domain.entity.HistorialArreglo;
import com.example.mecanic.bussines.domain.entity.Mecanico;
import com.example.mecanic.bussines.domain.entity.Vehiculo;
import com.example.mecanic.bussines.domain.entity.Usuario;
import com.example.mecanic.bussines.logic.service.HistorialArregloService;
import com.example.mecanic.bussines.logic.service.MecanicoService;
import com.example.mecanic.bussines.logic.service.VehiculoService;
import com.example.mecanic.bussines.logic.error.ErrorServiceException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/historial")
public class HistorialArregloController {

    @Autowired
    private MecanicoService mecanicoService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private HistorialArregloService historialService;

    // Alta y modificación
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("item") HistorialArreglo historial,
                             RedirectAttributes attributes,
                             Model model,
                             HttpSession session) {
        try {
            Usuario usuario = session != null ? (Usuario) session.getAttribute("usuariosession") : null;
            if (usuario == null) {
                attributes.addFlashAttribute("msgError", "La sesión expiró. Vuelva a iniciar sesión para continuar.");
                return "redirect:/vehiculo/list";
            }
            if (historial.getId() == null) {
                historialService.alta(historial, usuario.getId());
            } else {
                historialService.modificar(historial.getId(), historial, usuario.getId());
            }
            Long idVehiculo = historial.getVehiculo().getId();
            attributes.addFlashAttribute("msgExito", "Historial guardado correctamente.");
            return "redirect:/historial/list/" + idVehiculo;
        } catch (ErrorServiceException e) {
            populateForm(model, historial, false, session);
            model.addAttribute("msgError", e.getMessage());
            return "eHistorial.html";
        } catch (Exception e) {
            populateForm(model, historial, false, session);
            model.addAttribute("msgError", "Error del sistema");
            return "eHistorial.html";
        }
    }

    // Formulario de alta
    @GetMapping("/alta")
    public String crear(@RequestParam("idVehiculo") Long idVehiculo, Model model, HttpSession session, RedirectAttributes attributes) {

        try {
            Vehiculo vehiculo = vehiculoService.obtener(idVehiculo)
                    .orElseThrow(() -> new ErrorServiceException("El vehículo solicitado no existe o está inactivo."));
            Usuario usuario = session != null ? (Usuario) session.getAttribute("usuariosession") : null;
            if (usuario == null) {
                attributes.addFlashAttribute("msgError", "La sesión expiró. Vuelva a iniciar sesión para registrar un arreglo.");
                return "redirect:/vehiculo/list";
            }
            Mecanico mecanico = null;
            try {
                mecanico = mecanicoService.obtenerMecanicoPorUser(usuario.getId());
            } catch (ErrorServiceException ex) {
                // Usuario sin mecánico vinculado, se mostrará listado para elegir
            }
            HistorialArreglo nuevoHistorial = new HistorialArreglo();
            nuevoHistorial.setVehiculo(vehiculo);
            if (mecanico != null) {
                nuevoHistorial.setMecanico(mecanico);
            }
            populateForm(model, nuevoHistorial, false, session);
            return "eHistorial.html";
        } catch (ErrorServiceException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/historial/list/" + idVehiculo;
        } catch (Exception e) {
            attributes.addFlashAttribute("msgError", "Error del sistema");
            return "redirect:/historial/list/" + idVehiculo;
        }

    }

    // Listado
    @GetMapping("/list/{id}")
    public String listar(@PathVariable Long id, Model model) {
        try {
            Vehiculo vehiculo = vehiculoService.obtener(id)
                    .orElseThrow(() -> new ErrorServiceException("El vehículo solicitado no existe o está inactivo."));
            model.addAttribute("items", historialService.listarActivos(id));
            model.addAttribute("vehiculo", vehiculo);
            model.addAttribute("vehiculoDescripcion", buildVehiculoDescripcion(vehiculo));
            model.addAttribute("idVehiculo", id);
            return "lHistorial.html";
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            return "inicio.html";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error del sistema");
            return "inicio.html";
        }
    }

    // Modificación
    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        try {
            HistorialArreglo historial = historialService.obtenerHistorial(id);
            populateForm(model, historial, false, null);
            return "eHistorial.html";
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            return "eHistorial.html";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            return "eHistorial.html";
        }
    }

    // Consulta
    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable Long id, Model model) {
        try {
            HistorialArreglo historial = historialService.obtenerHistorial(id);
            populateForm(model, historial, true, null);
            return "eHistorial.html";
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            return "eHistorial.html";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            return "eHistorial.html";
        }
    }

    // Baja
    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes attributes, Model model) {
        try {
            
            Long idVehiculo = historialService.eliminar(id);
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/historial/list/" + idVehiculo;
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            return "eHistorial.html";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            return "eHistorial.html";
        }
    }

    private void populateForm(Model model, HistorialArreglo historial, boolean isDisabled, HttpSession session) {
        boolean mecanicoEditable = false;
        try {
            Vehiculo vehiculo = null;
            if (historial.getVehiculo() != null && historial.getVehiculo().getId() != null) {
                vehiculo = vehiculoService.obtener(historial.getVehiculo().getId())
                        .orElse(historial.getVehiculo());
            }
            if (vehiculo != null) {
                model.addAttribute("vehiculo", vehiculo);
                model.addAttribute("vehiculoDescripcion", buildVehiculoDescripcion(vehiculo));
            }

            Mecanico mecanico = historial.getMecanico();
            if (mecanico == null && session != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuariosession");
                if (usuario != null) {
                    try {
                        mecanico = mecanicoService.obtenerMecanicoPorUser(usuario.getId());
                        historial.setMecanico(mecanico);
                    } catch (ErrorServiceException ex) {
                        mecanicoEditable = true;
                    }
                }
            }

            if (mecanico != null) {
                model.addAttribute("mecanico", mecanico);
                model.addAttribute("mecanicoDescripcion", buildMecanicoDescripcion(mecanico));
            } else if (!isDisabled) {
                mecanicoEditable = true;
                List<Mecanico> mecanicosDisponibles = mecanicoService.listarActivos();
                model.addAttribute("mecanicos", mecanicosDisponibles);
            }
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("msgError", "Error al preparar el formulario del historial.");
        }
        model.addAttribute("mecanicoEditable", mecanicoEditable);
        model.addAttribute("item", historial);
        model.addAttribute("isDisabled", isDisabled);
    }

    private String buildVehiculoDescripcion(Vehiculo vehiculo) {
        return vehiculo.getMarca() + " " + vehiculo.getModelo() + " - " + vehiculo.getPatente();
    }

    private String buildMecanicoDescripcion(Mecanico mecanico) {
        return mecanico.getNombre() + " " + mecanico.getApellido();
    }
}

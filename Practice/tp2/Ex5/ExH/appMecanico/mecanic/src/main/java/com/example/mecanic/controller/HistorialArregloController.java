package com.example.mecanic.controller;

import com.example.mecanic.bussines.domain.entity.HistorialArreglo;
import com.example.mecanic.bussines.domain.entity.Mecanico;
import com.example.mecanic.bussines.domain.entity.Usuario;
import com.example.mecanic.bussines.domain.entity.Vehiculo;
import com.example.mecanic.bussines.domain.enumeration.Rol;
import com.example.mecanic.bussines.logic.error.ErrorServiceException;
import com.example.mecanic.bussines.logic.service.HistorialArregloService;
import com.example.mecanic.bussines.logic.service.MecanicoService;
import com.example.mecanic.bussines.logic.service.VehiculoService;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

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
            Usuario usuario = (Usuario) session.getAttribute("usuariosession");
            if (historial.getId() == null) {
                historialService.alta(historial,usuario.getId());
            } else {
                historialService.modificar(historial.getId(), historial,usuario.getId());
            }
            Long idVehiculo = historial.getVehiculo().getId();
            attributes.addFlashAttribute("msgExito", "Mecánico guardado correctamente.");
            return "redirect:/historial/list/" + idVehiculo;
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            model.addAttribute("item", historial);
            model.addAttribute("isDisabled", false);
            return "eHistorial.html";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error del sistema");
            model.addAttribute("item", historial);
            model.addAttribute("isDisabled", false);
            return "eHistorial.html";
        }
    }

    // Formulario de alta
    @GetMapping("/alta/{id}")
    public String crear(@PathVariable Long idVehiculo,HistorialArreglo historial, Model model,HttpSession session) {

        try{
            model.addAttribute("item", historial);
            Vehiculo v = vehiculoService.obtener(idVehiculo).get();
            Usuario usuario = (Usuario) session.getAttribute("usuariosession");
            Mecanico m = mecanicoService.obtenerMecanicoPorUser(usuario.getId());
            model.addAttribute("vehiculo",v);
            model.addAttribute("mecanico",m);
            model.addAttribute("isDisabled", false);
            return "eHistorial.html";
        }catch(ErrorServiceException e){
            model.addAttribute("msgError", e.getMessage());
            return "redirect:/historial/list/" + idVehiculo;
        }catch (Exception e) {
            model.addAttribute("msgError", "Error del sistema");
            return "redirect:/historial/list/"+ idVehiculo;
        }

    }

    // Listado
    @GetMapping("/list/{id}")
    public String listar(@PathVariable Long id, Model model) {
        try {
          
            model.addAttribute("items", historialService.listarActivos(id));
         
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
            model.addAttribute("item", historial);
            model.addAttribute("isDisabled", false);
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
            return "redirect:/historial/list/"+ idVehiculo;
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            return "eHistorial.html";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            return "eHistorial.html";
        }
    }
}

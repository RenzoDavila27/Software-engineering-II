package com.example.demo.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.business.domain.Estudio;
import com.example.demo.business.logic.error.ErrorServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collection;
import org.springframework.ui.Model;

import com.example.demo.business.logic.service.EstudioService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EstudioController {

    @Autowired
    private EstudioService estudioService;

    private String viewList = "view/estudio/lEstudio";
    private String redirectList = "redirect:/estudio/listaEstudio";


    @GetMapping("/estudio/listaEstudio")
    public String listarEstudio(Model model) {
        try {

            Collection<Estudio> listaEstudio = estudioService.listarEstudioActivo();

            /*
             * Transferencia de datos a la vista: En Spring MVC, la interfaz Model (de org.springframework.ui.Model), las clases ModelMap y ModelAndView
             * se utilizan para transferir datos del controlador a la vista.
             * Model: Esta interfaz proporciona una forma sencilla de añadir atributos (pares clave-valor) al modelo, a los que se puede acceder desde la vista.
             * ModelMap: Esta clase extiende Model y ofrece una funcionalidad similar, pero con una estructura similar a la de un mapa, lo que permite una
             *           gestión de atributos más flexible.
             * ModelAndView: Esta clase actúa como contenedor de los datos del modelo y del nombre de la vista, lo que permite que se devuelvan juntos desde
             *               un método del controlador.
             */
            model.addAttribute("listaEstudio", listaEstudio);
            model.addAttribute("estudio", new Estudio());

        }catch(ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
        }catch(Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
        }
        return viewList;    //"redirect:/estudio/listaEstudio"
    }

    @PostMapping("/estudio/guardar")
    public String guardarEstudio(Estudio est, BindingResult result, RedirectAttributes attributes, Model model){

        try {

            if (result.hasErrors()){
                model.addAttribute("msgError", "Error de Sistema");
                return viewList;       //"view/pais/ePais.html"
            }

            if (est.getId() == null)
                estudioService.crearEstudio(est.getNombre(), true);

            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return redirectList;      //"redirect:/pais/listPais"

        }catch(ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            return viewList; //"view/pais/ePais.html"
        }catch(Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            return viewList; //"view/pais/ePais.html"
        }


    }
    
    @PostMapping("/estudio/editar")
public String editarEstudioSubmit(
        @RequestParam("id") Long id,
        @RequestParam("nombre") String nombre,
        RedirectAttributes attributes,
        Model model) {

    try {
        estudioService.editarEstudio(id, nombre);
        attributes.addFlashAttribute("msgExito", "Estudio actualizado correctamente.");
        return "redirect:/estudio/listaEstudio";
    } catch (ErrorServiceException e) {
        model.addAttribute("msgError", e.getMessage());
        return "view/estudio/lEstudio";
    } catch (Exception e) {
        model.addAttribute("msgError", "Error de Sistema");
        return "view/estudio/lEstudio";
    }
}


    @GetMapping("/estudio/nuevo")
    public String nuevoEstudio(Model model) {
        model.addAttribute("estudio", new Estudio());
        return "view/estudio/lEstudio"; // o la vista donde está el modal
    }

    @GetMapping("/estudio/eliminar/{id}")
    public String eliminarEstudio(@PathVariable Long id) throws ErrorServiceException {
        estudioService.eliminarEstudio(id);
        return redirectList;
    }

}

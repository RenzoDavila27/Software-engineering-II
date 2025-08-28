package com.example.demo.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.business.domain.Categoria;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.logic.service.CategoriaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collection;
import org.springframework.ui.Model;

@Controller
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    private String viewList = "view/categoria/lCategoria";
    private String redirectList = "redirect:/categoria/listaCategoria";


    @GetMapping("/categoria/listaCategoria")
    public String listarCategoria(Model model) {
        try {

            Collection<Categoria> listaCategoria = categoriaService.listarCategoriaActiva();

            /*
             * Transferencia de datos a la vista: En Spring MVC, la interfaz Model (de org.springframework.ui.Model), las clases ModelMap y ModelAndView
             * se utilizan para transferir datos del controlador a la vista.
             * Model: Esta interfaz proporciona una forma sencilla de añadir atributos (pares clave-valor) al modelo, a los que se puede acceder desde la vista.
             * ModelMap: Esta clase extiende Model y ofrece una funcionalidad similar, pero con una estructura similar a la de un mapa, lo que permite una
             *           gestión de atributos más flexible.
             * ModelAndView: Esta clase actúa como contenedor de los datos del modelo y del nombre de la vista, lo que permite que se devuelvan juntos desde
             *               un método del controlador.
             */
            model.addAttribute("listaCategoria", listaCategoria);
            model.addAttribute("categoria", new Categoria());

        }catch(ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
        }catch(Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
        }
        return viewList;    //"redirect:/pais/listPais"
    }

    @PostMapping("/categoria/guardar")
    public String guardarCategoria(Categoria cat, BindingResult result, RedirectAttributes attributes, Model model){

        try {

            if (result.hasErrors()){
                model.addAttribute("msgError", "Error de Sistema");
                return viewList;       //"view/pais/ePais.html"
            }

            if (cat.getId() == null)
                categoriaService.crearCategoria(cat.getNombre());

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

    @GetMapping("/categoria/nuevo")
    public String nuevaCategoria(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "view/categoria/lCategoria"; // o la vista donde está el modal
    }

    @GetMapping("/categoria/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) throws ErrorServiceException {
        categoriaService.eliminarCategoria(id);
        return redirectList;
    }

}

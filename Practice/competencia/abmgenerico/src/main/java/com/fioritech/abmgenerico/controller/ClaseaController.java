package com.example.abmgenerico.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.abmgenerico.business.domain.Clasea;
import com.example.abmgenerico.business.logic.error.ErrorServiceException;
import com.example.abmgenerico.business.logic.service.ClaseaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collection;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClaseaController {

    @Autowired
    private ClaseaService claseaService;
    /*@Autowired
    private CategoriaService categoriaService;
    @Autowired
    private EstudioService estudioService;*/

    private String viewList = "view/clasea/lClasea";
	private String redirectList = "redirect:/clasea/listaClasea";
    

    @GetMapping("/clasea/listaClasea")
	public String listarClasea(Model model) {
		try {
			  
		  Collection<Clasea> listaClasea = claseaService.listarClaseaActivo();

		  /* 
		   * Transferencia de datos a la vista: En Spring MVC, la interfaz Model (de org.springframework.ui.Model), las clases ModelMap y ModelAndView 
		   * se utilizan para transferir datos del controlador a la vista.
           * Model: Esta interfaz proporciona una forma sencilla de añadir atributos (pares clave-valor) al modelo, a los que se puede acceder desde la vista.
           * ModelMap: Esta clase extiende Model y ofrece una funcionalidad similar, pero con una estructura similar a la de un mapa, lo que permite una 
           *           gestión de atributos más flexible.
           * ModelAndView: Esta clase actúa como contenedor de los datos del modelo y del nombre de la vista, lo que permite que se devuelvan juntos desde 
           *               un método del controlador.
		   */
		  model.addAttribute("listaClasea", listaClasea);
		  model.addAttribute("clasea", new Clasea());

		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema");  
		}
		return viewList;    //"redirect:/pais/listPais"
	}

	@PostMapping("/clasea/guardar")
	public String guardarClasea(Clasea juego, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewList;
		  }
		 
		  if (juego.getId() == null)
		   claseaService.crearClasea(juego.getStr1(),juego.getRutaimg(),juego.getFloat1(),juego.getShort1(),juego.getStr2(),juego.getBool1(),juego.getStr3());
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;
		  
		}catch(ErrorServiceException e) {	
			  model.addAttribute("msgError", e.getMessage());
			  return viewList;
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
			  return viewList;
		}
		

	}
        
    
    @PostMapping("/clasea/editar")
public String editarClaseaSubmit(
        @RequestParam("id") Long id,
        @RequestParam("str1") String str1,
        @RequestParam("rutaimg") String rutaimg,
        @RequestParam("float1") float float1,
        @RequestParam("short1") Short short1,
        @RequestParam("str2") String str2,
        @RequestParam("bool1") Boolean bool1,
        @RequestParam("str3") String str3,
        RedirectAttributes attributes,
        Model model) {

    try {
        claseaService.editarClasea(id, str1, rutaimg, float1, short1, str2,
                bool1, str3);
        attributes.addFlashAttribute("msgExito", "Clasea actualizado correctamente.");
        return "redirect:/clasea/listaClasea";
    } catch (ErrorServiceException e) {
        model.addAttribute("msgError", e.getMessage());
        return "view/clasea/lClasea";
    } catch (Exception e) {
        model.addAttribute("msgError", "Error de Sistema");
        return "view/clasea/lClasea";
    }
}

	@GetMapping("/clasea/nuevo")
	public String nuevoClasea(Model model) {
    	model.addAttribute("clasea", new Clasea());
    	return "view/clasea/lClasea"; // o la vista donde está el modal
	}

    @GetMapping("/clasea/eliminar/{id}")
    public String eliminarClasea(@PathVariable Long id) throws ErrorServiceException {
        claseaService.eliminarClasea(id);
        return redirectList;
    }
	
  @GetMapping("/clasea/buscar")
    public String buscarClasea(Model model, @RequestParam(value = "query", required = false) String query) {
        try {
            Collection<Clasea> listaClasea = claseaService.buscarClaseaPorStr1(query);
            model.addAttribute("listaClasea", listaClasea);
            model.addAttribute("clasea", new Clasea());
            return "view/clasea/lClasea";
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            model.addAttribute("clasea", new Clasea());
            return "view/clasea/lClasea";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            model.addAttribute("clasea", new Clasea());
            return "view/clasea/lClasea";
        }
    }

}

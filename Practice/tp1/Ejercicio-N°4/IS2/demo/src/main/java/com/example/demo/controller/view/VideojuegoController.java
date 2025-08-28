package com.example.demo.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.business.domain.Videojuego;
import com.example.demo.business.domain.Categoria;
import com.example.demo.business.domain.Estudio;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.logic.service.VideojuegoService;
import com.example.demo.business.logic.service.CategoriaService;
import com.example.demo.business.logic.service.EstudioService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collection;
import org.springframework.ui.Model;

@Controller
public class VideojuegoController {

    @Autowired
   	private VideojuegoService videojuegoService;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private EstudioService estudioService;

    private String viewList = "view/videojuego/lVideojuego";
	private String redirectList = "redirect:/videojuego/listaVideojuego";
    

    @GetMapping("/videojuego/listaVideojuego")
	public String listarVideojuego(Model model) {
		try {
			  
		  Collection<Videojuego> listaVideojuego = videojuegoService.listarVideojuegoActivo();
          Collection<Categoria> categorias = categoriaService.listarCategoriaActiva();
          Collection<Estudio> estudios = estudioService.listarEstudioActivo();
		  /* 
		   * Transferencia de datos a la vista: En Spring MVC, la interfaz Model (de org.springframework.ui.Model), las clases ModelMap y ModelAndView 
		   * se utilizan para transferir datos del controlador a la vista.
           * Model: Esta interfaz proporciona una forma sencilla de añadir atributos (pares clave-valor) al modelo, a los que se puede acceder desde la vista.
           * ModelMap: Esta clase extiende Model y ofrece una funcionalidad similar, pero con una estructura similar a la de un mapa, lo que permite una 
           *           gestión de atributos más flexible.
           * ModelAndView: Esta clase actúa como contenedor de los datos del modelo y del nombre de la vista, lo que permite que se devuelvan juntos desde 
           *               un método del controlador.
		   */
		  model.addAttribute("listaVideojuego", listaVideojuego);
		  model.addAttribute("videojuego", new Videojuego());
          model.addAttribute("categorias", categorias);
          model.addAttribute("estudios", estudios);

		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema");  
		}
		return viewList;    //"redirect:/pais/listPais"
	}

	@PostMapping("/videojuego/guardar")
	public String guardarVideojuego(Videojuego juego, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewList;
		  }
		 
		  if (juego.getId() == null)
		   videojuegoService.crearVideojuego(juego.getTitulo(),juego.getRutaimg(),juego.getPrecio(),juego.getCantidad(),juego.getDescripcion(),juego.getOferta(),juego.getFechalanzamiento(),juego.getCategoria(), juego.getEstudio());
			  
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

	@GetMapping("/videojuego/nuevo")
	public String nuevoVideojuego(Model model) {
    	model.addAttribute("videojuego", new Videojuego());
    	return "view/videojuego/lVideojuego"; // o la vista donde está el modal
	}

    @GetMapping("/videojuego/eliminar/{id}")
    public String eliminarVideojuego(@PathVariable Long id) throws ErrorServiceException {
        videojuegoService.eliminarVideojuego(id);
        return redirectList;
    }
	
}

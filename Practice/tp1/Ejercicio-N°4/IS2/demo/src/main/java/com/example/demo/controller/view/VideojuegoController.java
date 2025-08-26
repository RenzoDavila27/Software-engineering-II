package com.example.demo.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.business.domain.Videojuego;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.logic.service.VideojuegoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collection;
import org.springframework.ui.Model;

@Controller
public class VideojuegoController {

    @Autowired
   	private VideojuegoService videojuegoService;

    private String viewList = "view/videojuego/lVideojuego";
	private String redirectList = "redirect:/videojuego/listaVideojuego";
    

    @GetMapping("/videojuego/listaVideojuego")
	public String listarVideojuego(Model model) {
		try {
			  
		  Collection<Videojuego> listaVideojuego = videojuegoService.listarVideojuegoActivo();
		  
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
			return viewList;       //"view/pais/ePais.html"
		  }
		 
		  if (juego.getId() == null)
		   videojuegoService.crearVideojuego(juego.getTitulo(),juego.getRutaimg(),juego.getPrecio(),juego.getCantidad(),juego.getDescripcion(),juego.getOferta(),juego.getFechalanzamiento());
			  
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

	@GetMapping("/videojuego/nuevo")
	public String nuevoVideojuego(Model model) {
    	model.addAttribute("videojuego", new Videojuego());
    	return "view/videojuego/lVideojuego"; // o la vista donde está el modal
	}
	
}

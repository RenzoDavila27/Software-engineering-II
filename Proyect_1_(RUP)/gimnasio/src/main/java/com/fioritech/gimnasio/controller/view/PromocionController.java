package com.fioritech.gimnasio.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fioritech.gimnasio.business.domain.Promocion;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.PromocionService;

@Controller
public class PromocionController {
    
    @Autowired
    private PromocionService service;

    private String viewEdit = "view/promociones/ePromociones";
    private String viewList = "view/promociones/lPromociones";
    private String redirectList = "redirect:/promocion/listaPromocion";


    @GetMapping("/promocion/listaPromocion")
    public String listaPromocion(Model model)  throws BusinessException{
        try{
            List<Promocion> listaPromocion = service.listarPromocionActivo();
            model.addAttribute("listaPromocion",listaPromocion);
            return viewList;
        }catch(BusinessException e){
          model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema"); 
		}
        return viewList;
    }

    @GetMapping("/promocion/altaPromocion")
	public String alta(Promocion promocion, Model model) {
		model.addAttribute("isDisabled", false);
        model.addAttribute("promocion",promocion);
		return viewEdit;                         
	}

    @GetMapping("/promocion/consultar/{id}")
	public String consultar(@PathVariable("id") String idPromocion, Model model) {
		
		try {
			
		  Promocion promocion = service.buscarPromocion(idPromocion);		
		  model.addAttribute("promocion", promocion);
		  model.addAttribute("isDisabled", true);

		  return viewEdit;                  
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                       
		}		  
	}

    @GetMapping("/promocion/modificar/{id}")
	public String modificar(@PathVariable("id") String idPromocion, Model model) {
		
		try {
			
		  Promocion promocion = service.buscarPromocion(idPromocion);		
		  model.addAttribute("promocion", promocion);
		  model.addAttribute("isDisabled", false);
		  return viewEdit;                     
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                    
		}		  
	}

    @GetMapping("/promocion/baja/{id}")
	public String baja(@PathVariable("id") String idPromocion, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  service.eliminarPromocion(idPromocion);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;                                         
		  
		}catch(BusinessException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;                                        
		} 
	}

    @PostMapping("/promocion/aceptarEditPromocion")
	public String aceptarEdit(Promocion promocion, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;      
		  }
		 
		  if (promocion.getId() == null || promocion.getId().trim().isEmpty())
		   service.crearPromocion(promocion.getUsuario().getId(),promocion.getFechaEnvioPromocion(),promocion.getTitulo(),promocion.getTexto());
		  else 
		   service.modificarPromocion(promocion.getId(),promocion.getUsuario().getId(),promocion.getFechaEnvioPromocion(),promocion.getTitulo(),promocion.getTexto());
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;      
		  
		}catch(BusinessException e) {	
			  model.addAttribute("msgError", e.getMessage());
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
		}
		return viewEdit; 
		
	}

    @GetMapping("/promocion/cancelarEditPromocion")
	public String cancelarEdit() {
		return redirectList;                     
	}


}

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

import com.fioritech.gimnasio.business.domain.Pais;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.PaisService;

@Controller
public class PaisController {

    @Autowired
    private PaisService service;

    @GetMapping("/pais/listaPais")
    public String listaPaises(Model model)  throws BusinessException{
        try{
            List<Pais> listaPais = service.listarPaisActivo();
            model.addAttribute("listaPais",listaPais);
            return "view/Direcciones/pais/lPais";
        }catch(BusinessException e){
          model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema"); 
		}
        return "view/Direcciones/pais/lPais";
    }

    @GetMapping("/pais/altaPais")
	public String alta(Pais pais, Model model) {
		model.addAttribute("isDisabled", false);
		return "view/Direcciones/pais/ePais";                            
	}

    @GetMapping("/pais/consultar/{id}")
	public String consultar(@PathVariable("id") String idPais, Model model) {
		
		try {
			
		  Pais pais = service.buscarPais(idPais);		
		  model.addAttribute("pais", pais);
		  model.addAttribute("isDisabled", true);
		  
		  return "view/Direcciones/pais/ePais";                 
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return "redirect:/pais/listaPais";  
		}		  
	}


    @GetMapping("/pais/modificar/{id}")
	public String modificar(@PathVariable("id") String idPais, Model model) {
		
		try {
			
		  Pais pais = service.buscarPais(idPais);		
		  model.addAttribute("pais", pais);
		  model.addAttribute("isDisabled", false);
		  
		  return "view/Direcciones/pais/ePais";                      
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return "redirect:/pais/listaPais";               
		}		  
	}
	
	
	
	@GetMapping("/pais/baja/{id}")
	public String baja(@PathVariable("id") String idPais, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  service.eliminarPais(idPais);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return "redirect:/pais/listaPais";                                        
		  
		}catch(BusinessException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return "redirect:/pais/listaPais";                                         
		} 

    }

    @PostMapping("/pais/aceptarEditPais")
	public String aceptarEdit(Pais pais, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return "view/Direcciones/pais/ePais";        
		  }
		 
		  if (pais.getId() == null || pais.getId().trim().isEmpty())
		    service.crearPais(pais.getNombre());
		  else 
		   service.modificarPais(pais.getId(), pais.getNombre());
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return "redirect:/pais/listaPais";      
		  
		}catch(BusinessException e) {	
			  model.addAttribute("msgError", e.getMessage());
			  
			  return "view/Direcciones/pais/ePais"; 
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
			  return "view/Direcciones/pais/ePais";
		}
		
	}
	
	
	
	@GetMapping("/pais/cancelarEditPais")
	public String cancelarEdit() {
	    return "redirect:/pais/listaPais";                  
	}
    
}

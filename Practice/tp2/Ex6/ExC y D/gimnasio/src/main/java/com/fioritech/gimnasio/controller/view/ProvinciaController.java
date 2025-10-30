package com.fioritech.gimnasio.controller.view;

import java.util.Collection;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fioritech.gimnasio.business.domain.Provincia;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.PaisService;
import com.fioritech.gimnasio.business.logic.service.ProvinciaService;

@Controller
public class ProvinciaController {

    private String viewEdit = "view/Direcciones/provincia/eProvincia";
    private String viewList = "view/Direcciones/provincia/lProvincia";
    private String redirectList = "redirect:/provincia/listaProvincia";

    @Autowired
    private ProvinciaService service;

    @Autowired
    private PaisService paisService;

    @GetMapping("/provincia/listaProvincia")
    public String listaProvincias(Model model)  throws BusinessException{
        try{
            Collection<Provincia> listaProvincia = service.listarProvinciaActiva();
            model.addAttribute("listaProvincia",listaProvincia);
            return viewList;
        }catch(BusinessException e){
          model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema"); 
		}
        return viewList;
    }

    @GetMapping("/provincia/altaProvincia")
	public String alta(Provincia provincia, Model model) {
		model.addAttribute("isDisabled", false);
        model.addAttribute("listaPais",paisService.listarPaisActivo());
		return viewEdit;                         
	}

    @GetMapping("/provincia/consultar/{id}")
	public String consultar(@PathVariable("id") String idProvincia, Model model) {
		
		try {
			
		  Provincia provincia = service.buscarProvincia(idProvincia);		
		  model.addAttribute("provincia", provincia);
          model.addAttribute("listaPais",paisService.listarPaisActivo());
		  model.addAttribute("isDisabled", true);

		  return viewEdit;                  
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                       
		}		  
	}

    @GetMapping("/provincia/modificar/{id}")
	public String modificar(@PathVariable("id") String idProvincia, Model model) {
		
		try {
			
		  Provincia provincia = service.buscarProvincia(idProvincia);		
		  model.addAttribute("provincia", provincia);
          model.addAttribute("listaPais",paisService.listarPaisActivo());
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit;                      //"view/pais/ePais.html"
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                     //"redirect:/pais/listPais"
		}		  
	}

    @GetMapping("/provincia/baja/{id}")
	public String baja(@PathVariable("id") String idProvincia, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  service.eliminarProvincia(idProvincia);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;                                         
		  
		}catch(BusinessException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;                                        
		} 
	}

    @PostMapping("/provincia/aceptarEditProvincia")
	public String aceptarEdit(Provincia provincia, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;      
		  }
		 
		  if (provincia.getId() == null || provincia.getId().trim().isEmpty())
		   service.crearProvincia(provincia.getNombre(),provincia.getPais().getId());
		  else 
		   service.modificarProvincia(provincia.getId(), provincia.getNombre(), provincia.getPais().getId());
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;      
		  
		}catch(BusinessException e) {	
			  model.addAttribute("msgError", e.getMessage());
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
		}
        model.addAttribute("listaPais",paisService.listarPaisActivo());
		return viewEdit; 
		
	}

    @GetMapping("/provincia/cancelarEditProvincia")
	public String cancelarEdit() {
		return redirectList;                     
	}



    
}

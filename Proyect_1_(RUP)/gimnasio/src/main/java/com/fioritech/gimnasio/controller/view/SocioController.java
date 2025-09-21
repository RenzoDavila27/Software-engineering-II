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

import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.enums.TipoDocumento;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.SocioService;

@Controller
public class SocioController {

    @Autowired
    private SocioService service;

    private void cargarCatalogos(Model model) {
        model.addAttribute("tiposDocumento", TipoDocumento.values());
    }

    @GetMapping("/socio/listaSocio")
    public String listaSocios(Model model)  throws BusinessException{
        try{
            List<Socio> listaSocio = service.listarSocioActivo();
            model.addAttribute("listaSocio",listaSocio);
            cargarCatalogos(model);
            return "view/socio/lSocio";
        }catch(BusinessException e){
          model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema"); 
		}
        return "view/socio/lSocio";
    }

    @GetMapping("/socio/altaSocio")
	public String alta(Socio socio, Model model) {
		model.addAttribute("isDisabled", false);
		cargarCatalogos(model);
		return "view/socio/eSocio";                            
	}

    @GetMapping("/socio/consultar/{id}")
	public String consultar(@PathVariable("id") String idSocio, Model model) {
		
		try {
			
		  Socio socio = service.buscarSocio(idSocio);		
		  model.addAttribute("socio", socio);
		  model.addAttribute("isDisabled", true);
		  cargarCatalogos(model);
		  return "view/socio/eSocio";                 
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return "view/socio/lSocio";  
		}		  
	}


    @GetMapping("/socio/modificar/{id}")
	public String modificar(@PathVariable("id") String idSocio, Model model) {
		
		try {
			
		  Socio socio = service.buscarSocio(idSocio);		
		  model.addAttribute("socio", socio);
		  model.addAttribute("isDisabled", false);
		  cargarCatalogos(model);
		  
		  return "view/socio/eSocio";                      
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return "view/socio/lSocio";               
		}		  
	}
	
	
	
	@GetMapping("/socio/baja/{id}")
	public String baja(@PathVariable("id") String idSocio, RedirectAttributes attributes) {	
		
		try {
			
		  service.eliminarSocio(idSocio);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return "redirect:/socio/listaSocio";                                        
		  
		}catch(BusinessException e) {	
		   attributes.addFlashAttribute("msgError", e.getMessage());
		   return "redirect:/socio/listaSocio";                                         
		} 

    }

    @PostMapping("/socio/aceptarEditSocio")
	public String aceptarEdit(Socio socio, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			cargarCatalogos(model);
			return "view/socio/eSocio";        
		  }
		 
		  if (socio.getId() == null || socio.getId().trim().isEmpty())
		    service.crearSocio(socio.getNombre(), socio.getApellido(), socio.getFechaNacimiento(),
            socio.getTipoDocumento(), socio.getNumeroDocumento(), socio.getTelefono(), socio.getCorreoElectronico(),
            socio.getNumeroSocio());
		  else 
		   service.modificarSocio(socio.getId(), socio.getNombre(), socio.getApellido(),
           socio.getFechaNacimiento(), socio.getTipoDocumento(), socio.getNumeroDocumento(), socio.getTelefono(), 
           socio.getCorreoElectronico(), socio.getNumeroSocio());
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return "redirect:/socio/listaSocio";      
		  
		}catch(BusinessException e) {	
			  model.addAttribute("msgError", e.getMessage());
			  cargarCatalogos(model);
			  return "view/socio/eSocio"; 
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
			  cargarCatalogos(model);
			  return "view/socio/eSocio";
		}
		
	}
	
	@GetMapping("/socio/cancelarEditSocio")
	public String cancelarEdit() {
	    return "redirect:/socio/listaSocio";                   
	}

}

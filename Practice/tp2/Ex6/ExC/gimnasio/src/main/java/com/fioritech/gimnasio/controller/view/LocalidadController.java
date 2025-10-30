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

import com.fioritech.gimnasio.business.domain.Departamento;
import com.fioritech.gimnasio.business.domain.Localidad;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.DepartamentoService;
import com.fioritech.gimnasio.business.logic.service.LocalidadService;
import com.fioritech.gimnasio.business.logic.service.PaisService;
import com.fioritech.gimnasio.business.logic.service.ProvinciaService;

@Controller
public class LocalidadController {

    @Autowired
    private LocalidadService service;

    @Autowired
    private ProvinciaService provinciaService;

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private PaisService paisService;

    private String viewEdit = "view/Direcciones/localidad/eLocalidad";
    private String viewList = "view/Direcciones/localidad/lLocalidad";
    private String redirectList = "redirect:/localidad/listaLocalidad";
    
    @GetMapping("/localidad/listaLocalidad")
    public String listaLocalidades(Model model)  throws BusinessException{
        try{
            Collection<Localidad> listaLocalidad = service.listarLocalidadActivo();
            model.addAttribute("listaLocalidad",listaLocalidad);
            return viewList;
        }catch(BusinessException e){
          model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema"); 
		}
        return viewList;
    }

    @GetMapping("/localidad/altaLocalidad")
	public String alta(Localidad localidad, Model model) {
		model.addAttribute("isDisabled", false);
        model.addAttribute("localidad", new Localidad());
        model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
        model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
        model.addAttribute("listaPais",paisService.listarPaisActivo());
		return viewEdit;                         
	}

    @GetMapping("/localidad/consultar/{id}")
	public String consultar(@PathVariable("id") String idLocalidad, Model model) {
		
		try {
			
		  Localidad localidad = service.buscarLocalidad(idLocalidad);		
		  model.addAttribute("localidad", localidad);
          model.addAttribute("listaPais",paisService.listarPaisActivo());
          model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
          model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
		  model.addAttribute("isDisabled", true);

		  return viewEdit;                  
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                       
		}		  
	}

    @GetMapping("/localidad/modificar/{id}")
	public String modificar(@PathVariable("id") String idLocalidad, Model model) {
		
		try {
			
		  Localidad localidad = service.buscarLocalidad(idLocalidad);		
		  model.addAttribute("localidad", localidad);
          model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
          model.addAttribute("listaPais",paisService.listarPaisActivo());
          model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit;                     
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                    
		}		  
	}

    @GetMapping("/localidad/baja/{id}")
	public String baja(@PathVariable("id") String idLocalidad, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  service.eliminarLocalidad(idLocalidad);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;                                         
		  
		}catch(BusinessException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;                                        
		} 
	}

    @PostMapping("/localidad/aceptarEditLocalidad")
	public String aceptarEdit(Localidad localidad, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;      
		  }
		 
		  if (localidad.getId() == null || localidad.getId().trim().isEmpty())
		   service.crearLocalidad(localidad.getNombre(), localidad.getCodigoPostal(), localidad.getDepartamento().getId());
		  else 
		   service.modificarLocalidad(localidad.getId(),localidad.getNombre(), localidad.getCodigoPostal(), localidad.getDepartamento().getId());
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;      
		  
		}catch(BusinessException e) {	
			  model.addAttribute("msgError", e.getMessage());
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
		}
        model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
		model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
        model.addAttribute("listaPais",paisService.listarPaisActivo());
		return viewEdit; 
		
	}

    @GetMapping("/localidad/cancelarEditLocalidad")
	public String cancelarEdit() {
		return redirectList;                     
	}
}

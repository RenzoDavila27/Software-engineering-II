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
import com.fioritech.gimnasio.business.domain.Provincia;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.DepartamentoService;
import com.fioritech.gimnasio.business.logic.service.PaisService;
import com.fioritech.gimnasio.business.logic.service.ProvinciaService;

@Controller
public class DepartamentoController {
    private String viewEdit = "view/Direcciones/departamento/eDepartamento";
    private String viewList = "view/Direcciones/departamento/lDepartamento";
    private String redirectList = "redirect:/departamento/listaDepartamento";

    @Autowired
    private DepartamentoService service;

    @Autowired
    private ProvinciaService provinciaService;

    @Autowired
    private PaisService paisService;

    @GetMapping("/departamento/listaDepartamento")
    public String listaDepartamentos(Model model)  throws BusinessException{
        try{
            Collection<Departamento> listaDepartamento = service.listarDepartamentoActivo();
            model.addAttribute("listaDepartamento",listaDepartamento);
            return viewList;
        }catch(BusinessException e){
          model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema"); 
		}
        return viewList;
    }

    @GetMapping("/departamento/altaDepartamento")
	public String alta(Departamento departamento, Model model) {
		model.addAttribute("isDisabled", false);
        model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
        model.addAttribute("listaPais",paisService.listarPaisActivo());
		return viewEdit;                         
	}

    @GetMapping("/departamento/consultar/{id}")
	public String consultar(@PathVariable("id") String idDepartamento, Model model) {
		
		try {
			
		  Departamento departamento = service.buscarDepartamento(idDepartamento);		
		  model.addAttribute("departamento", departamento);
          model.addAttribute("listaPais",paisService.listarPaisActivo());
          model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
		  model.addAttribute("isDisabled", true);

		  return viewEdit;                  
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                       
		}		  
	}

    @GetMapping("/departamento/modificar/{id}")
	public String modificar(@PathVariable("id") String idDepartamento, Model model) {
		
		try {
			
		  Departamento departamento = service.buscarDepartamento(idDepartamento);		
		  model.addAttribute("departamento", departamento);
          model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
          model.addAttribute("listaPais",paisService.listarPaisActivo());
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit;                     
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                    
		}		  
	}

    @GetMapping("/departamento/baja/{id}")
	public String baja(@PathVariable("id") String idDepartamento, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  service.eliminarDepartamento(idDepartamento);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;                                         
		  
		}catch(BusinessException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;                                        
		} 
	}

    @PostMapping("/departamento/aceptarEditDepartamento")
	public String aceptarEdit(Departamento departamento, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;      
		  }
		 
		  if (departamento.getId() == null || departamento.getId().trim().isEmpty())
		   service.crearDepartamento(departamento.getNombre(),departamento.getProvincia().getId());
		  else 
		   service.modificarDepartamento(departamento.getId(),departamento.getNombre(),departamento.getProvincia().getId());
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;      
		  
		}catch(BusinessException e) {	
			  model.addAttribute("msgError", e.getMessage());
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
		}
		model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
        model.addAttribute("listaPais",paisService.listarPaisActivo());
		return viewEdit; 
		
	}

    @GetMapping("/departamento/cancelarEditDepartamento")
	public String cancelarEdit() {
		return redirectList;                     
	}
}

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
import com.fioritech.gimnasio.business.domain.Empresa;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.EmpresaService;

@Controller
public class EmpresaController {

    private String viewEdit = "view/Empresa/eEmpresa";
    private String viewList = "view/Empresa/lEmpresa";
    private String redirectList = "redirect:/empresa/listaEmpresa";

    @Autowired
    private EmpresaService service;

    @GetMapping("/empresa/listaEmpresa")
    public String listaEmpresa(Model model)  throws BusinessException{
        try{
            Collection<Empresa> listaEmpresa = service.listarEmpresaActiva();
            model.addAttribute("listaEmpresa",listaEmpresa);
            return viewList;
        }catch(BusinessException e){
          model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema"); 
		}
        return viewList;
    }

    @GetMapping("/empresa/altaEmpresa")
	public String alta(Empresa empresa, Model model) {
		model.addAttribute("isDisabled", false);
        model.addAttribute("empresa", empresa);
		return viewEdit;                         
	}

    @GetMapping("/empresa/consultar/{id}")
	public String consultar(@PathVariable("id") String idEmpresa, Model model) {
		
		try {
			
		  Empresa empresa = service.buscarEmpresa(idEmpresa);		
		  model.addAttribute("empresa", empresa);
		  model.addAttribute("isDisabled", true);

		  return viewEdit;                  
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                       
		}		  
	}

    @GetMapping("/empresa/modificar/{id}")
	public String modificar(@PathVariable("id") String idEmpresa, Model model) {
		
		try {
			
		  Empresa empresa = service.buscarEmpresa(idEmpresa);		
		  model.addAttribute("empresa", empresa);
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit;                     
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                    
		}		  
	}

    @GetMapping("/empresa/baja/{id}")
	public String baja(@PathVariable("id") String idEmpresa, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  service.eliminarEmpresa(idEmpresa);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;                                         
		  
		}catch(BusinessException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;                                        
		} 
	}

    @PostMapping("/empresa/aceptarEditEmpresa")
	public String aceptarEdit(Empresa empresa, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;      
		  }
		 
		  if (empresa.getId() == null || empresa.getId().trim().isEmpty())
		   service.crearEmpresa(empresa.getNombre(),empresa.getTelefono(),empresa.getCorreoElectronico());
		  else 
		   service.modificarEmpresa(empresa.getId(),empresa.getNombre(),empresa.getTelefono(),empresa.getCorreoElectronico());
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;      
		  
		}catch(BusinessException e) {	
			  model.addAttribute("msgError", e.getMessage());
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
		}
		return viewEdit; 
		
	}

    @GetMapping("/empresa/cancelarEditEmpresa")
	public String cancelarEdit() {
		return redirectList;                     
	}
}

package com.fioritech.gimnasio.controller.view;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fioritech.gimnasio.business.domain.Direccion;
import com.fioritech.gimnasio.business.domain.Empresa;
import com.fioritech.gimnasio.business.domain.Sucursal;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.DepartamentoService;
import com.fioritech.gimnasio.business.logic.service.EmpresaService;
import com.fioritech.gimnasio.business.logic.service.LocalidadService;
import com.fioritech.gimnasio.business.logic.service.PaisService;
import com.fioritech.gimnasio.business.logic.service.ProvinciaService;
import com.fioritech.gimnasio.business.logic.service.SucursalService;

@Controller
public class SucursalController {

    @Autowired
    private SucursalService service;

    @Autowired
    private PaisService paisService;

    @Autowired
    private ProvinciaService provinciaService;

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private LocalidadService localidadService;

    @Autowired
    private EmpresaService empresaService;



    private String viewEdit = "view/Sucursal/eSucursal";
    private String viewList = "view/Sucursal/lSucursal";
    private String redirectList = "redirect:/sucursal/listaSucursal";


    @GetMapping("/sucursal/listaSucursal")
    public String listaSucursal(Model model)  throws BusinessException{
        try{
            List<Sucursal> listaSucursal = service.listarSucursalActiva();
            model.addAttribute("listaSucursal",listaSucursal);
            return viewList;
        }catch(BusinessException e){
          model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema"); 
		}
        return viewList;
    }

    @GetMapping("/sucursal/altaSucursal")
	public String alta(Model model) {
		model.addAttribute("isDisabled", false);
        model.addAttribute("listaEmpresa", empresaService.listarEmpresaActiva());
        model.addAttribute("listaPais", paisService.listarPaisActivo());
        model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
        model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
        model.addAttribute("listaLocalidad", localidadService.listarLocalidadActivo());
        Sucursal sucursal = new Sucursal();
		sucursal.setDireccion(new Direccion()); 
		model.addAttribute("sucursal", sucursal);
		return viewEdit;                         
	}

    @GetMapping("/sucursal/consultar/{id}")
	public String consultar(@PathVariable("id") String idSucursal, Model model) {
		
		try {
			
		  Sucursal sucursal = service.buscarSucursal(idSucursal);		
		  model.addAttribute("sucursal", sucursal);
          model.addAttribute("listaEmpresa", empresaService.listarEmpresaActiva());
            model.addAttribute("listaPais", paisService.listarPaisActivo());
            model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
            model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
            model.addAttribute("listaLocalidad", localidadService.listarLocalidadActivo());
		  model.addAttribute("isDisabled", true);

		  return viewEdit;                  
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                       
		}		  
	}

    @GetMapping("/sucursal/modificar/{id}")
	public String modificar(@PathVariable("id") String idSucursal, Model model) {
		
		try {
			
		  Sucursal sucursal = service.buscarSucursal(idSucursal);		
		  model.addAttribute("sucursal", sucursal);
          model.addAttribute("direccion", sucursal.getDireccion());
          model.addAttribute("listaEmpresa", empresaService.listarEmpresaActiva());
            model.addAttribute("listaPais", paisService.listarPaisActivo());
            model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
            model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
            model.addAttribute("listaLocalidad", localidadService.listarLocalidadActivo());
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit;                     
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                    
		}		  
	}

    @GetMapping("/sucursal/baja/{id}")
	public String baja(@PathVariable("id") String idSucursal, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  service.eliminarSucursal(idSucursal);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;                                         
		  
		}catch(BusinessException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;                                        
		} 
	}

    @PostMapping("/sucursal/aceptarEditSucursal")
	public String aceptarEdit(Sucursal sucursal, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;      
		  }
		 
		  if (sucursal.getId() == null || sucursal.getId().trim().isEmpty())
		   service.crearSucursal(sucursal.getNombre(), sucursal.getEmpresa().getId(),sucursal.getDireccion());
		  else 
		   service.modificarSucursal(sucursal.getId(),sucursal.getNombre(),sucursal.getEmpresa().getId(),sucursal.getDireccion());
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;      
		  
		}catch(BusinessException e) {	
			  model.addAttribute("msgError", e.getMessage());
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
		}
        model.addAttribute("listaEmpresa", empresaService.listarEmpresaActiva());
        model.addAttribute("listaPais", paisService.listarPaisActivo());
        model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
        model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
        model.addAttribute("listaLocalidad", localidadService.listarLocalidadActivo());
		return viewEdit; 
		
	}

    @GetMapping("/sucursal/cancelarEditSucursal")
	public String cancelarEdit() {
		return redirectList;                     
	}


}

package com.fioritech.gimnasio.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fioritech.gimnasio.business.domain.Direccion;
import com.fioritech.gimnasio.business.domain.Empleado;
import com.fioritech.gimnasio.business.domain.Localidad;
import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.RolUsuario;
import com.fioritech.gimnasio.business.domain.enums.TipoDocumento;
import com.fioritech.gimnasio.business.domain.enums.TipoEmpleado;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.DepartamentoService;
import com.fioritech.gimnasio.business.logic.service.EmpleadoService;
import com.fioritech.gimnasio.business.logic.service.EmpresaService;
import com.fioritech.gimnasio.business.logic.service.LocalidadService;
import com.fioritech.gimnasio.business.logic.service.PaisService;
import com.fioritech.gimnasio.business.logic.service.ProvinciaService;
import com.fioritech.gimnasio.business.logic.service.SocioService;
import com.fioritech.gimnasio.business.logic.service.SucursalService;

@Controller
public class SocioController {

    @Autowired
    private SocioService service;

    private String viewEdit = "view/usuario/eSocio";
    private String viewList = "view/usuario/lSocio";
    private String redirectList = "redirect:/socio/listaSocio";

    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private SucursalService sucursalService;
    @Autowired
    private PaisService paisService;
    @Autowired
    private ProvinciaService provinciaService;
    @Autowired
    private DepartamentoService departamentoService;
    @Autowired
    private LocalidadService localidadService;
   
    @GetMapping("/socio/listaSocio")
    public String listaSocio(Model model) {
        try {
            List<Socio> listaSocio = service.listarSocioActivo();
            model.addAttribute("listaSocio", listaSocio);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
        }
        return viewList;
    }


    @GetMapping("/socio/altaSocio")
	public String alta(Socio socio, Model model) {
		model.addAttribute("isDisabled", false);

        socio.setUsuario(new Usuario());
        Direccion direccion = new Direccion();
		direccion.setLocalidad(new Localidad());
        socio.setDireccion(direccion);
        
        model.addAttribute("socio",socio);
        //CARGO LOS ENUMS
        model.addAttribute("tipoDocumento", TipoDocumento.values());
        model.addAttribute("rolUsuario", RolUsuario.values());

        //ACA LE PASO LOS DATOS DE DONDE TRABAJA Y LA DIRECCION :)
        model.addAttribute("listaEmpresa", empresaService.listarEmpresaActiva());
        model.addAttribute("listaSucursal", sucursalService.listarSucursalActiva());
        model.addAttribute("listaPais", paisService.listarPaisActivo());
        model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
        model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
        model.addAttribute("listaLocalidad", localidadService.listarLocalidadActivo());
		return viewEdit;                         
	}

    @GetMapping("/socio/consultar/{id}")
	public String consultar(@PathVariable("id") String idSocio, Model model) {
		
		try {
			
		  Socio socio = service.buscarSocio(idSocio);		
		  model.addAttribute("socio", socio);
		  model.addAttribute("isDisabled", true);
           //CARGO LOS ENUMS
            model.addAttribute("tipoDocumento", TipoDocumento.values());
            model.addAttribute("rolUsuario", RolUsuario.values());

            //ACA LE PASO LOS DATOS DE DONDE TRABAJA Y LA DIRECCION :)
            model.addAttribute("listaEmpresa", empresaService.listarEmpresaActiva());
            model.addAttribute("listaSucursal", sucursalService.listarSucursalActiva());
            model.addAttribute("listaPais", paisService.listarPaisActivo());
            model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
            model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
            model.addAttribute("listaLocalidad", localidadService.listarLocalidadActivo());
            
		  return viewEdit;                  
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                       
		}		  
	}
       @GetMapping("/socio/modificar/{id}")
	public String modificar(@PathVariable("id") String idSocio, Model model) {
		
		try {
			
		    Socio socio = service.buscarSocio(idSocio);		
		    model.addAttribute("socio", socio);
            //CARGO LOS ENUMS
            model.addAttribute("tipoDocumento", TipoDocumento.values());
            model.addAttribute("rolUsuario", RolUsuario.values());

            //ACA LE PASO LOS DATOS DE DONDE TRABAJA Y LA DIRECCION :)
            model.addAttribute("listaEmpresa", empresaService.listarEmpresaActiva());
            model.addAttribute("listaSucursal", sucursalService.listarSucursalActiva());
            model.addAttribute("listaPais", paisService.listarPaisActivo());
            model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
            model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
            model.addAttribute("listaLocalidad", localidadService.listarLocalidadActivo());
		  
		  return viewEdit;                     
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;                    
		}		  
	}

    @GetMapping("/socio/baja/{id}")
	public String baja(@PathVariable("id") String idSocio, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  service.eliminarSocio(idSocio);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;                                         
		  
		}catch(BusinessException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;                                        
		} 
	}

    @PostMapping("/socio/aceptarEditSocio")
	public String aceptarEdit(@RequestParam("sucursalSeleccionada") String idSucursal,Socio socio, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;      
		  }
		 
		  if (socio.getId() == null || socio.getId().trim().isEmpty()){
		   service.crearSocio(idSucursal,socio.getNombre(),socio.getApellido(),socio.getFechaNacimiento(),socio.getTipoDocumento(),socio.getNumeroDocumento(),socio.getTelefono(),socio.getCorreoElectronico(),socio.getUsuario(),socio.getDireccion());
		  }else{
		   	//service.modificarSocio(socio.getId(),socio);
         }
         attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		 return redirectList;
		  
		}catch(BusinessException e) {	
			  model.addAttribute("msgError", e.getMessage());
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
		}
        //CARGO LOS ENUMS
        model.addAttribute("tipoDocumento", TipoDocumento.values());
        model.addAttribute("rolUsuario", RolUsuario.values());

        //ACA LE PASO LOS DATOS DE DONDE TRABAJA Y LA DIRECCION :)
        model.addAttribute("listaEmpresa", empresaService.listarEmpresaActiva());
        model.addAttribute("listaSucursal", sucursalService.listarSucursalActiva());
        model.addAttribute("listaPais", paisService.listarPaisActivo());
        model.addAttribute("listaProvincia", provinciaService.listarProvinciaActiva());
        model.addAttribute("listaDepartamento", departamentoService.listarDepartamentoActivo());
        model.addAttribute("listaLocalidad", localidadService.listarLocalidadActivo());
		return viewEdit; 
		
	}

    @GetMapping("/socio/cancelarEditSocio")
	public String cancelarEdit() {
		return redirectList;                     
	}

}

package com.fioritech.gimnasio.controller.view;

import com.fioritech.gimnasio.business.domain.Departamento;
import com.fioritech.gimnasio.business.domain.Direccion;
import com.fioritech.gimnasio.business.domain.Empleado;
import com.fioritech.gimnasio.business.domain.Localidad;
import com.fioritech.gimnasio.business.domain.Sucursal;
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
import com.fioritech.gimnasio.business.logic.service.SucursalService;
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

@Controller
public class EmpleadoController {

    private String viewEdit = "view/usuario/eEmpleado";
    private String viewList = "view/usuario/lEmpleado";
    private String redirectList = "redirect:/empleado/listaEmpleado";


    @Autowired
    private EmpleadoService service;
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
   
    @GetMapping("/empleado/listaEmpleado")
    public String listaEmpleado(Model model) {
        try {
            List<Empleado> listaEmpleado = service.listarEmpleadoActivo();
            model.addAttribute("listaEmpleado", listaEmpleado);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
        }
        return viewList;
    }


    @GetMapping("/empleado/altaEmpleado")
	public String alta(Empleado empleado, Model model) {
		model.addAttribute("isDisabled", false);

        empleado.setUsuario(new Usuario());
        Direccion direccion = new Direccion();
        direccion.setLocalidad(new Localidad());
        empleado.setDireccion(direccion);
        
        model.addAttribute("empleado",empleado);
        //CARGO LOS ENUMS
        model.addAttribute("tipoDocumento", TipoDocumento.values());
        model.addAttribute("tipoEmpleado", TipoEmpleado.values());
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

    @GetMapping("/empleado/consultar/{id}")
	public String consultar(@PathVariable("id") String idEmpleado, Model model) {
		
		try {
			
		  Empleado empleado = service.buscarEmpleado(idEmpleado);		
		  model.addAttribute("empleado", empleado);
		  model.addAttribute("isDisabled", true);
           //CARGO LOS ENUMS
            model.addAttribute("tipoDocumento", TipoDocumento.values());
            model.addAttribute("tipoEmpleado", TipoEmpleado.values());
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

    @GetMapping("/empleado/modificar/{id}")
	public String modificar(@PathVariable("id") String idEmpleado, Model model) {
		
		try {
			
		    Empleado empleado = service.buscarEmpleado(idEmpleado);		
		    model.addAttribute("empleado", empleado);
            System.out.println("Empleado: " + empleado.getId());
            System.out.println("Usuario: " + (empleado.getUsuario() != null ? empleado.getUsuario().getId() : "NULL"));
            System.out.println("Direccion: " + (empleado.getDireccion() != null ? empleado.getDireccion().getId() : "NULL"));
            
            //CARGO LOS ENUMS
            model.addAttribute("tipoDocumento", TipoDocumento.values());
            model.addAttribute("tipoEmpleado", TipoEmpleado.values());
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

    @GetMapping("/empleado/baja/{id}")
	public String baja(@PathVariable("id") String idEmpleado, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  service.eliminarEmpleado(idEmpleado);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;                                         
		  
		}catch(BusinessException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;                                        
		} 
	}

    @PostMapping("/empleado/aceptarEditEmpleado")
	public String aceptarEdit(@RequestParam("sucursalSeleccionada") String idSucursal,Empleado empleado, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;      
		  }
		 
		  if (empleado.getId() == null || empleado.getId().trim().isEmpty()){
		   service.crearEmpleado(idSucursal,empleado.getNombre(),empleado.getApellido(),empleado.getFechaNacimiento(),empleado.getTipoDocumento(),empleado.getNumeroDocumento(),empleado.getTelefono(),empleado.getCorreoElectronico(),empleado.getTipoEmpleado(),empleado.getUsuario(),empleado.getDireccion());
		  }else{
           System.out.println("ESTOY ENTRANDO AL MODIFICAR");
           System.out.println("ID EMPLEADO: "+empleado.getId()); 
           System.out.println("Nombre EMPLEADO: "+empleado.getNombre());   
           System.out.println("Sucursal Seleccionada: "+idSucursal);
		   service.modificarEmpleado(empleado.getId(),empleado.getNombre(),empleado.getApellido(),empleado.getFechaNacimiento(),empleado.getTipoDocumento(),empleado.getNumeroDocumento(),empleado.getTelefono(),empleado.getCorreoElectronico(),empleado.getTipoEmpleado(),idSucursal,empleado.getUsuario(),empleado.getDireccion());
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
        model.addAttribute("tipoEmpleado", TipoEmpleado.values());
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

    @GetMapping("/empleado/cancelarEditEmpleado")
	public String cancelarEdit() {
		return redirectList;                     
	}

}
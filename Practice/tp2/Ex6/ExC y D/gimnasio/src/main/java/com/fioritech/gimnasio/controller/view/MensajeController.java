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

import com.fioritech.gimnasio.business.domain.Mensaje;
import com.fioritech.gimnasio.business.domain.Promocion;
import com.fioritech.gimnasio.business.domain.enums.RolUsuario;
import com.fioritech.gimnasio.business.domain.enums.TipoMensaje;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.MensajeService;
import com.fioritech.gimnasio.business.logic.service.PromocionService;
import com.fioritech.gimnasio.business.logic.service.UsuarioService;

@Controller
public class MensajeController {
    
     @Autowired
    private MensajeService service;

     @Autowired
    private PromocionService promocionService;

    @Autowired
    private UsuarioService usuarioService;

    private String viewEdit = "view/promociones/eMensaje";
    private String viewList = "view/promociones/lPromociones";
    private String redirectList = "redirect:/mensaje/listaMensaje";


    @GetMapping("/mensaje/listaMensaje")
    public String listaPromocion(Model model)  throws BusinessException{
        try{
            List<Mensaje> listaMensaje = service.listarMensajeActivo();
            List<Promocion> listaPromocion = promocionService.listarPromocionActivo();
            model.addAttribute("listaPromocion",listaPromocion);
            model.addAttribute("listaMensaje",listaMensaje);
            return viewList;
        }catch(BusinessException e){
          model.addAttribute("msgError2", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError2", "Error de Sistema"); 
		}
      return viewList;
    }

    @GetMapping("/mensaje/altaMensaje")
	public String alta(Mensaje mensaje, Model model) {
		model.addAttribute("isDisabled", false);
        model.addAttribute("mensaje",mensaje);
        model.addAttribute("tipoMensaje", TipoMensaje.values());
        model.addAttribute("listaUsuario",usuarioService.listarUsuariosPorTipo(RolUsuario.ADMINISTRADOR));
		return viewEdit;                         
	}

    @GetMapping("/mensaje/consultar/{id}")
	public String consultar(@PathVariable("id") String idMensaje, Model model) {
		
		try {
			
		  Mensaje mensaje = service.buscarMensaje(idMensaje);		
		  model.addAttribute("mensaje", mensaje);
		  model.addAttribute("isDisabled", true);
           model.addAttribute("listaUsuario",usuarioService.listarUsuariosPorTipo(RolUsuario.ADMINISTRADOR));
        model.addAttribute("tipoMensaje", TipoMensaje.values());

		  return viewEdit;                  
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError2", e.getMessage());
		  return viewList;                       
		}		  
	}

    @GetMapping("/mensaje/modificar/{id}")
	public String modificar(@PathVariable("id") String idMensaje, Model model) {
		
		try {
			
		  Mensaje mensaje = service.buscarMensaje(idMensaje);		
		  model.addAttribute("mensaje", mensaje);
		  model.addAttribute("isDisabled", false);
           model.addAttribute("listaUsuario",usuarioService.listarUsuariosPorTipo(RolUsuario.ADMINISTRADOR));
            model.addAttribute("tipoMensaje", TipoMensaje.values());
		  return viewEdit;                     
		 
		}catch(BusinessException e) {	
		  model.addAttribute("msgError2", e.getMessage());
		  return viewList;                    
		}		  
	}

    @GetMapping("/mensaje/baja/{id}")
	public String baja(@PathVariable("id") String idMensaje, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  service.eliminarMensaje(idMensaje);		
		  attributes.addFlashAttribute("msgExito2", "La acción fue realizada correctamente.");
		  return redirectList;                                         
		  
		}catch(BusinessException e) {	
		   model.addAttribute("msgError2", e.getMessage());
		   return redirectList;                                        
		} 
	}

    @PostMapping("/mensaje/aceptarEditMensaje")
	public String aceptarEdit(Mensaje mensaje,@RequestParam("idUsuario")String idUsuario, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
            System.out.println("ENTRO A LOS ERRORES");
			model.addAttribute("msgError2", "Error de Sistema");     
		  }
		 
		  if (mensaje.getId() == null || mensaje.getId().trim().isEmpty()){
            System.out.println("ENTRO A CREAR");
		   service.crearMensaje(idUsuario,mensaje.getTitulo(),mensaje.getTexto(),mensaje.getTipoMensaje());
		  }else{
		   service.modificarMensaje(mensaje.getId(),idUsuario,mensaje.getTitulo(),mensaje.getTexto(),mensaje.getTipoMensaje());
            }
		  attributes.addFlashAttribute("msgExito2", "La acción fue realizada correctamente.");
		  return redirectList;      
		  
		}catch(BusinessException e) {	
			  model.addAttribute("msgError2", e.getMessage());
		}catch(Exception e) {
			  model.addAttribute("msgError2", "Error de Sistema");
		}
        model.addAttribute("listaUsuario",usuarioService.listarUsuariosPorTipo(RolUsuario.ADMINISTRADOR));
        model.addAttribute("tipoMensaje", TipoMensaje.values());
		return viewEdit; 
		
	}

    @GetMapping("/mensaje/cancelarEditMensaje")
	public String cancelarEdit() {
		return redirectList;                     
	}

    @GetMapping("/mensaje/enviarMensaje/{id}")
    public String enviarMensaje(@PathVariable("id") String idMensaje, RedirectAttributes attributes, Model model) {
        try {
            service.enviarMensaje(idMensaje);
            attributes.addFlashAttribute("msgExito", "El mensaje fue enviado correctamente.");
            return redirectList;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return redirectList;
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            return redirectList;
        }
    }

}

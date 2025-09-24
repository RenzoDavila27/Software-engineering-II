package com.fioritech.gimnasio.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fioritech.gimnasio.business.domain.Mensaje;
import com.fioritech.gimnasio.business.domain.Promocion;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.MensajeService;
import com.fioritech.gimnasio.business.logic.service.PromocionService;

@Controller
public class MensajeController {
    
     @Autowired
    private MensajeService service;

    private String viewEdit = "view/promociones/eMensaje";
    private String viewList = "view/promociones/lMensajes";
    private String redirectList = "redirect:/mensaje/listaMensaje";


    @GetMapping("/mensaje/listaMensaje")
    public String listaMensaje(Model model)  throws BusinessException{
        try{
            List<Mensaje> listaMensaje = service.listarMensajeActivo();
            model.addAttribute("listaMensaje",listaMensaje);
            return viewList;
        }catch(BusinessException e){
          model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema"); 
		}
        return viewList;
    }

}


package com.is.biblioteca.controller.view;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.is.biblioteca.business.domain.entity.Usuario;
import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.logic.service.UsuarioService;
import com.is.biblioteca.business.logic.service.LibroService;

@Controller
@RequestMapping("/imagen")
public class ImagenController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private LibroService libroService;
    
    @GetMapping("/perfil/{id}")
    public ResponseEntity<byte[]> imagenUsuario (@PathVariable String id){
    	
      try {  
    	  
       Usuario usuario = usuarioService.buscarUsuario(id);
        
       if (usuario == null || usuario.getImagen() == null) {
           return ResponseEntity.notFound().build();
       }

       byte[] imagen= usuario.getImagen().getContenido();
       
       HttpHeaders headers = new HttpHeaders();
       MediaType mediaType = MediaType.IMAGE_JPEG;
       if (usuario.getImagen().getMime() != null) {
           mediaType = MediaType.parseMediaType(usuario.getImagen().getMime());
       }
       headers.setContentType(mediaType);

       return new ResponseEntity<>(imagen,headers, HttpStatus.OK); 
       
      }catch(Exception e) {
       return null;	  
      } 
    }

    @GetMapping("/libro/{id}")
    public ResponseEntity<byte[]> imagenLibro(@PathVariable String id) {
        try {
            Libro libro = libroService.buscarLibro(id);

            if (libro == null || libro.getImagen() == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] imagen = libro.getImagen().getContenido();

            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = MediaType.IMAGE_JPEG;
            if (libro.getImagen().getMime() != null) {
                mediaType = MediaType.parseMediaType(libro.getImagen().getMime());
            }
            headers.setContentType(mediaType);

            return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

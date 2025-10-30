package com.tinder.demo.controller.view;

import com.tinder.demo.bussines.domain.Mascota;
import com.tinder.demo.bussines.domain.Sexo;
import com.tinder.demo.bussines.domain.Tipo;
import com.tinder.demo.bussines.domain.Usuario;
import com.tinder.demo.bussines.domain.Zona;
import com.tinder.demo.bussines.logic.service.MascotaService;
import com.tinder.demo.bussines.logic.service.UsuarioService;
import com.tinder.demo.bussines.logic.service.ZonaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@Controller
@RequestMapping("/mascota")
public class MascotaController {

    @Autowired
    private MascotaService serviceMascota;

    @GetMapping("/mis-mascotas")
    public String listarMascotas(Model model, @AuthenticationPrincipal Usuario usuario) throws Exception{

        try {
        // Ahora puedes usar el objeto 'usuario' directamente,
        // sabiendo que nunca será null.
        Collection<Mascota> mascotas = serviceMascota.listarMascotasPorUsuario(usuario.getId());
        model.addAttribute("mascotas", mascotas);
        return "mascotas";
    } catch(Exception e) {
        e.printStackTrace();
        // (Considera redirigir a una página de error en lugar de 'inicio')
        return "inicio"; 
    }
    }

    @GetMapping("/editar-perfil")
    public String perfil(Model model, Long id, String accion, HttpSession session) throws Exception{

        Mascota mascota;

        if (accion.equals("Actualizar") || accion.equals("Eliminar") || accion.equals("Alta")){
            mascota = serviceMascota.buscarMascotaPorId(id);
        }else {
            mascota = new Mascota();

        }

        model.addAttribute("perfil", mascota);
        model.addAttribute("accion", accion);
        model.addAttribute("sexos", Sexo.values());
        model.addAttribute("tipos", Tipo.values());
        return "mascota";
    }

    @PostMapping("/actualizar-perfil")
    public String editarMascota(
            @RequestParam(required = false) Long id, 
            @RequestParam String nombre, 
            @RequestParam Tipo tipo, 
            @RequestParam Sexo sexo, 
            @RequestParam MultipartFile archivo, 
            Model model,
            @AuthenticationPrincipal Usuario usuario // <-- CAMBIO: Pedimos el usuario autenticado
    ) throws Exception {

        try {
            // Ya no necesitamos obtener el usuario de la sesión
            // Usuario usuario = (Usuario) session.getAttribute("usuariosession");
            // El filtro de seguridad garantiza que 'usuario' no será null aquí.

            byte[] imagenBytes = archivo.getBytes();
            String fotoTipo = archivo.getContentType();

            if (id == null) {
                // Usamos el ID del usuario inyectado
                serviceMascota.crearMascota(nombre, sexo, tipo, imagenBytes, fotoTipo, usuario.getId());
            } else {
                serviceMascota.modificarMascota(id, nombre, tipo, sexo, imagenBytes, fotoTipo);
            }

            return "redirect:/mascota/mis-mascotas";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al modificar la mascota");
            return "error";
        }
    }

    @PostMapping("/eliminar-perfil")
    public String eliminarMascota(@RequestParam Long id, Model model) throws Exception{
        try{

            serviceMascota.darDeBajaMascota(id);
            return "redirect:/mascota/mis-mascotas";

        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("error", "Error al eliminar la mascota");
            return "error";
        }
    }

    @PostMapping("/alta-perfil")
    public String altaMascota(@RequestParam Long id, Model model){
        try{

            serviceMascota.darDeAltaMascota(id);
            return "redirect:/mascota/mis-mascotas";

        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("error", "Error al dar de alta la mascota");
            return "error";
        }
    }

    @GetMapping("/debaja-mascotas")
    public String listarMascotasInactivas(
            Model model, 
            @AuthenticationPrincipal Usuario usuario // <-- CAMBIO
    ) throws Exception {
        
        // La comprobación manual de la sesión (if usuario == null) 
        // ya no es necesaria. El filtro de seguridad se encargó.

        try {
            // Usamos el 'usuario' inyectado directamente
            Collection<Mascota> mascotas = serviceMascota.listarMascotasInactivas(usuario.getId());
            model.addAttribute("mascotas", mascotas);
            return "mascotasdebaja";
        } catch(Exception e) {
            e.printStackTrace();
            return "inicio";
        }
    }

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> mostrarFoto(@PathVariable Long id) throws Exception{
        Mascota mascota = serviceMascota.buscarMascotaPorId(id);
        if (mascota.getFoto() != null && mascota.getFotoTipo() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mascota.getFotoTipo()))
                    .body(mascota.getFoto());
        } else {
            return ResponseEntity.notFound().build();  // o podrías devolver una imagen por defecto
        }
    }
}

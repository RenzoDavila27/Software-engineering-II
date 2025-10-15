package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.controller.template.CrudTemplateController;
import com.fioritech.demo.bussines.domain.Usuario;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@Controller
@RequestMapping("/usuario")
public class UsuarioController extends CrudTemplateController<Usuario, Long> {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    protected Collection<Usuario> listarEntidades() {
        return usuarioService.listarUsuarios();
    }

    @Override
    protected void crearEntidad(Usuario usuario) {
        usuarioService.crearUsuario(usuario);
    }

    @Override
    protected Usuario buscarEntidad(Long id) {
        return usuarioService.buscarUsuarioPorId(id);
    }

    @Override
    protected void modificarEntidad(Long id, Usuario cambios) {
        usuarioService.modificarUsuario(id, cambios);
    }

    @Override
    protected void eliminarEntidad(Long id) {
        usuarioService.eliminarUsuario(id);
    }

    @Override
    protected Usuario crearInstanciaFormulario() {
        return new Usuario();
    }

    @Override
    protected String obtenerNombreModeloListado() {
        return "listaUsuario";
    }

    @Override
    protected String obtenerNombreModeloFormulario() {
        return "usuario";
    }

    @Override
    protected String obtenerVistaListado() {
        return "usuario/listarUsuario";
    }

    @Override
    protected String obtenerVistaCreacion() {
        return "usuario/crearUsuario";
    }

    @Override
    protected String obtenerVistaEdicion() {
        return "usuario/modificarUsuario";
    }

    @Override
    protected String obtenerRedirectListado() {
        return "redirect:/usuario/listar";
    }

    @GetMapping("/crearForm")
    public String mostrarFormularioCreacion(Model model) {
        return super.mostrarFormularioCreacion(model);
    }

    @GetMapping("/modificarForm/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        return super.mostrarFormularioEdicion(id, model);
    }

    @PostMapping("/modificarClave")
    public String modificarClave(@RequestParam("cuenta") String cuenta,
                                 @RequestParam("clave") String clave,
                                 @RequestParam("clavenueva") String clavenueva) {
        usuarioService.modificarClave(cuenta, clave, clavenueva);
        return obtenerRedirectListado();
    }

    @PostMapping("/login")
    public String loginUsuario(@RequestParam("cuenta") String cuenta,
                               @RequestParam("clave") String clave,
                               ModelMap modelo,
                               HttpSession session) {
        try {
            Usuario usuario = usuarioService.login(cuenta, clave);
            session.setAttribute("usuarioSession", usuario);
            return "inicio";
        } catch (BusinessException ex) {
            modelo.put("msgError", ex.getMessage());
            return "login";
        } catch (Exception e) {
            modelo.put("msgError", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/cambiarClaveUsuario")
    public String cambiarClaveUsuarioForm() {
        return "usuario/cambioClaveUsuario";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @GetMapping("/volverEdit")
    public String volver() {
        return obtenerRedirectListado();
    }
}


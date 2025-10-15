package com.tienda.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tienda.app.business.domain.Proveedor;
import com.tienda.app.business.domain.Usuario;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.logic.service.ProveedorService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/proveedor")
public class ProveedorController extends BaseController<Proveedor, Long> {

    public ProveedorController(ProveedorService service) {
        super(service);
        initController(new Proveedor(), "LIST PROVEEDOR", "EDIT PROVEEDOR");
    }

    @Override
    @GetMapping("/list")
    public String listar(Model model) {
        if (!esAdmin()) {
            return "redirect:/login?redirect=/proveedor/list";
        }
        return super.listar(model);
    }

    @Override
    @GetMapping("/alta")
    public String crear(Proveedor entidad, Model model) {
        if (!esAdmin()) {
            return "redirect:/login?redirect=/proveedor/alta";
        }
        return super.crear(entidad, model);
    }

    @Override
    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable Long id, Model model) {
        if (!esAdmin()) {
            return "redirect:/login?redirect=/proveedor/consultar/" + id;
        }
        return super.consultar(id, model);
    }

    @Override
    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        if (!esAdmin()) {
            return "redirect:/login?redirect=/proveedor/modificar/" + id;
        }
        return super.editar(id, model);
    }

    @Override
    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes attributes, Model model) {
        if (!esAdmin()) {
            attributes.addFlashAttribute("msgError", "Debe iniciar sesión como administrador para acceder a proveedores.");
            return "redirect:/login?redirect=/proveedor/list";
        }
        return super.eliminar(id, attributes, model);
    }

    @Override
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("item") Proveedor entidad, RedirectAttributes attributes, Model model) {
        if (!esAdmin()) {
            attributes.addFlashAttribute("msgError", "Debe iniciar sesión como administrador para acceder a proveedores.");
            return "redirect:/login?redirect=/proveedor/list";
        }
        return super.actualizar(entidad, attributes, model);
    }

    @Override
    @GetMapping("/cancelar")
    public String cancelar() {
        if (!esAdmin()) {
            return "redirect:/login?redirect=/proveedor/list";
        }
        return super.cancelar();
    }

    @Override
    protected void preAlta() throws ErrorServiceException {
        // No additional pre-processing required.
    }

    @Override
    protected void preModificacion() throws ErrorServiceException {
        // No additional pre-processing required.
    }

    private boolean esAdmin() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }
        HttpServletRequest request = attributes.getRequest();
        Object usuarioObj = request.getSession(false) != null ? request.getSession(false).getAttribute("usuarioActual") : null;
        if (usuarioObj instanceof Usuario usuario) {
            return Boolean.TRUE.equals(usuario.getAdministrador());
        }
        return false;
    }
}

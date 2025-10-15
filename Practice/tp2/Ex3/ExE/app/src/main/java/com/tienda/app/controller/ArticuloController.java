package com.tienda.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tienda.app.business.domain.Articulo;
import com.tienda.app.business.domain.Proveedor;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.logic.service.ArticuloService;
import com.tienda.app.business.logic.service.ProveedorService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/articulo")
public class ArticuloController extends BaseController<Articulo, Long> {

    private final ProveedorService proveedorService;

    public ArticuloController(ArticuloService service, ProveedorService proveedorService) {
        super(service);
        this.proveedorService = proveedorService;
        initController(new Articulo(), "LIST ARTÍCULO", "EDIT ARTÍCULO");
    }

    @Override
    protected void preAlta() throws ErrorServiceException {
        ensureProveedorInstance();
        loadProveedores();
    }

    @Override
    protected void preModificacion() throws ErrorServiceException {
        ensureProveedorInstance();
        loadProveedores();
    }

    @Override
    protected void preActualziacion() throws ErrorServiceException {
        loadProveedores();
    }

    @Override
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("item") Articulo articulo, RedirectAttributes attributes, Model model) {
        try {
            if (articulo.getProveedor() != null && articulo.getProveedor().getId() != null) {
                Proveedor proveedor = proveedorService.obtenerEntidad(articulo.getProveedor().getId());
                articulo.setProveedor(proveedor);
            }
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            model.addAttribute("item", articulo);
            model.addAttribute("isDisabled", false);
            model.addAttribute("titleEdit", titleEdit);
            model.addAttribute("nameEntityLower", nameEntityLower);
            try {
                model.addAttribute("proveedores", proveedorService.listarActivos());
            } catch (ErrorServiceException ex) {
                model.addAttribute("msgError", ex.getMessage());
            }
            return viewEdit;
        }
        return super.actualizar(articulo, attributes, model);
    }

    private void ensureProveedorInstance() {
        if (entity != null && entity.getProveedor() == null) {
            entity.setProveedor(new Proveedor());
        }
    }

    private void loadProveedores() throws ErrorServiceException {
        if (model != null) {
            model.addAttribute("proveedores", proveedorService.listarActivos());
        }
    }
}

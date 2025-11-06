package com.example.frontend.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.frontend.business.domain.CategoriaDto;
import com.example.frontend.business.logic.error.ErrorServiceException;
import com.example.frontend.business.logic.service.CategoriaService;
import com.example.frontend.controller.view.form.CategoriaForm;

@Controller
@RequestMapping("/categorias")
public class CategoriaViewController extends BaseViewController<CategoriaDto, CategoriaForm, Long> {

    private final CategoriaService service;

    public CategoriaViewController(CategoriaService service) {
        super("view/categoria/lCategoria", "view/categoria/eCategoria", "redirect:/categorias", "Categorías", "Categoría");
        this.service = service;
    }

    @Override
    protected List<CategoriaDto> obtenerListado() throws ErrorServiceException {
        return service.listar();
    }

    @Override
    protected CategoriaDto obtenerPorId(Long id) throws ErrorServiceException {
        return service.obtener(id);
    }

    @Override
    protected void crearRegistro(CategoriaForm form) throws ErrorServiceException {
        service.crear(form);
    }

    @Override
    protected void actualizarRegistro(Long id, CategoriaForm form) throws ErrorServiceException {
        service.actualizar(id, form);
    }

    @Override
    protected void eliminarRegistro(Long id) throws ErrorServiceException {
        service.eliminar(id);
    }

    @Override
    protected CategoriaForm crearFormularioVacio() {
        return new CategoriaForm();
    }

    @Override
    protected CategoriaForm convertirAFormulario(CategoriaDto dto) {
        return service.toForm(dto);
    }

    @Override
    protected Long obtenerIdFormulario(CategoriaForm form) {
        return form.getId();
    }

    @Override
    protected void postListar(org.springframework.ui.Model model, List<CategoriaDto> items) {
        model.addAttribute("form", new CategoriaForm());
    }
}

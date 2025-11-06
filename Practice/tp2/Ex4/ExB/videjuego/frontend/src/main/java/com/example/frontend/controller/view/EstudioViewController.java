package com.example.frontend.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.frontend.business.domain.EstudioDto;
import com.example.frontend.business.logic.error.ErrorServiceException;
import com.example.frontend.business.logic.service.EstudioService;
import com.example.frontend.controller.view.form.EstudioForm;

@Controller
@RequestMapping("/estudios")
public class EstudioViewController extends BaseViewController<EstudioDto, EstudioForm, Long> {

    private final EstudioService service;

    public EstudioViewController(EstudioService service) {
        super("view/estudio/lEstudio", "view/estudio/eEstudio", "redirect:/estudios", "Estudios", "Estudio");
        this.service = service;
    }

    @Override
    protected List<EstudioDto> obtenerListado() throws ErrorServiceException {
        return service.listar();
    }

    @Override
    protected EstudioDto obtenerPorId(Long id) throws ErrorServiceException {
        return service.obtener(id);
    }

    @Override
    protected void crearRegistro(EstudioForm form) throws ErrorServiceException {
        service.crear(form);
    }

    @Override
    protected void actualizarRegistro(Long id, EstudioForm form) throws ErrorServiceException {
        service.actualizar(id, form);
    }

    @Override
    protected void eliminarRegistro(Long id) throws ErrorServiceException {
        service.eliminar(id);
    }

    @Override
    protected EstudioForm crearFormularioVacio() {
        return new EstudioForm();
    }

    @Override
    protected EstudioForm convertirAFormulario(EstudioDto dto) {
        return service.toForm(dto);
    }

    @Override
    protected Long obtenerIdFormulario(EstudioForm form) {
        return form.getId();
    }

    @Override
    protected void postListar(org.springframework.ui.Model model, List<EstudioDto> items) {
        model.addAttribute("form", new EstudioForm());
    }
}

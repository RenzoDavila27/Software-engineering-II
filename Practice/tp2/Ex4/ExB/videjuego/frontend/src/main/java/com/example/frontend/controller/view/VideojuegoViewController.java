package com.example.frontend.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.frontend.business.domain.VideojuegoDto;
import com.example.frontend.business.logic.error.ErrorServiceException;
import com.example.frontend.business.logic.service.CategoriaService;
import com.example.frontend.business.logic.service.EstudioService;
import com.example.frontend.business.logic.service.VideojuegoService;
import com.example.frontend.controller.view.form.VideojuegoForm;

@Controller
@RequestMapping("/videojuegos")
public class VideojuegoViewController extends BaseViewController<VideojuegoDto, VideojuegoForm, Long> {

    private final VideojuegoService service;
    private final CategoriaService categoriaService;
    private final EstudioService estudioService;

    public VideojuegoViewController(VideojuegoService service,
                                    CategoriaService categoriaService,
                                    EstudioService estudioService) {
        super("view/videojuego/lVideojuego", "view/videojuego/eVideojuego", "redirect:/videojuegos", "Videojuegos", "Videojuego");
        this.service = service;
        this.categoriaService = categoriaService;
        this.estudioService = estudioService;
    }

    @Override
    protected List<VideojuegoDto> obtenerListado() throws ErrorServiceException {
        return service.listar();
    }

    @Override
    protected VideojuegoDto obtenerPorId(Long id) throws ErrorServiceException {
        return service.obtener(id);
    }

    @Override
    protected void crearRegistro(VideojuegoForm form) throws ErrorServiceException {
        service.crear(form);
    }

    @Override
    protected void actualizarRegistro(Long id, VideojuegoForm form) throws ErrorServiceException {
        service.actualizar(id, form);
    }

    @Override
    protected void eliminarRegistro(Long id) throws ErrorServiceException {
        service.eliminar(id);
    }

    @Override
    protected VideojuegoForm crearFormularioVacio() {
        return new VideojuegoForm();
    }

    @Override
    protected VideojuegoForm convertirAFormulario(VideojuegoDto dto) {
        return service.toForm(dto);
    }

    @Override
    protected Long obtenerIdFormulario(VideojuegoForm form) {
        return form.getId();
    }

    @Override
    protected void postListar(Model model, List<VideojuegoDto> items) throws ErrorServiceException {
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estudios", estudioService.listar());
        model.addAttribute("form", new VideojuegoForm());
    }

    @Override
    protected void cargarDatosFormulario(Model model) throws ErrorServiceException {
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estudios", estudioService.listar());
    }
}

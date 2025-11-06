package org.consultorio.demo.bussiness.logic.service;

import lombok.AllArgsConstructor;
import org.consultorio.demo.bussiness.domain.FotoPaciente;
import org.consultorio.demo.bussiness.domain.Usuario;
import org.consultorio.demo.bussiness.logic.error.ServiceException;
import org.consultorio.demo.bussiness.persistance.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioService extends TemplateService<Usuario> {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FotoPacienteService fotoPacienteService;

    @Override
    protected JpaRepository<Usuario, String> getRepository() {
        return usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Usuario autenticar(String nombreUsuario, String clave) throws ServiceException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuarioAndClave(nombreUsuario, clave);
        if (usuarioOpt.isEmpty()) {
            throw new ServiceException("Usuario o contraseña incorrectos");
        }
        Usuario usuario = usuarioOpt.get();
        if (usuario.isEliminado()) {
            throw new ServiceException("Usuario inactivo");
        }
        return usuario;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    public FotoPaciente buscarFoto(Usuario usuario) {
        return fotoPacienteService.buscarPorUsuarioId(usuario.getId());
    }
}

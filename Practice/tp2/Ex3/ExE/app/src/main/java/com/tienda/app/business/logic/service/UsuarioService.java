package com.tienda.app.business.logic.service;

import org.springframework.stereotype.Service;

import com.tienda.app.business.domain.Usuario;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.persistence.repository.UsuarioRepository;

@Service
public class UsuarioService extends BaseService<Usuario, Long> {

    public UsuarioService(UsuarioRepository repository) {
        super(repository);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Usuario usuario) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {

                if (usuario == null) {
                    throw new ErrorServiceException("Debe indicar el usuario");
                }

                if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el nombre del usuario");
                }

                if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar la contraseña del usuario");
                }

                if (usuario.isEliminado()) {
                    throw new ErrorServiceException("El usuario indicado se encuentra eliminado");
                }

                if (usuario.getAdministrador() == null) {
                    usuario.setAdministrador(Boolean.FALSE);
                }

                Usuario usuarioExistente = ((UsuarioRepository) repository)
                        .buscarUsuarioPorNombre(usuario.getNombre());

                boolean usuarioActivo = usuarioExistente != null && !usuarioExistente.isEliminado();

                if (usuarioActivo && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("Existe un usuario con el nombre indicado");
                }

                if (usuarioActivo
                        && useCase == BaseUseCaseService.MODIFICACION
                        && !usuarioExistente.getId().equals(usuario.getId())) {
                    throw new ErrorServiceException("Existe un usuario con el nombre indicado");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public Usuario autenticar(String nombre, String password) throws ErrorServiceException {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el nombre de usuario");
            }

            if (password == null || password.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar la contraseña");
            }

            Usuario usuario = ((UsuarioRepository) repository).buscarUsuarioPorNombre(nombre);

            if (usuario == null || usuario.isEliminado()) {
                throw new ErrorServiceException("El usuario indicado no existe o se encuentra eliminado");
            }

            if (!usuario.getPassword().equals(password)) {
                throw new ErrorServiceException("Las credenciales ingresadas son inválidas");
            }

            return usuario;
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}

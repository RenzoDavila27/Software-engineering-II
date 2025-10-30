package com.example.mecanic.bussines.logic.service;

import com.example.mecanic.bussines.domain.entity.Mecanico;
import com.example.mecanic.bussines.persistence.repository.MecanicoRepository;
import com.example.mecanic.bussines.logic.service.UsuarioService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.mecanic.bussines.domain.entity.Usuario;
import com.example.mecanic.bussines.logic.service.BaseService;
import com.example.mecanic.bussines.logic.service.BaseUseCaseService;
import org.springframework.transaction.annotation.Transactional;

import com.example.mecanic.bussines.logic.error.ErrorServiceException;
import com.example.mecanic.bussines.domain.enumeration.Rol;
import org.springframework.stereotype.Service;

@Service
public class MecanicoService{
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MecanicoRepository repository;



    @Transactional
    public Mecanico alta(Mecanico mecanico,String nombre,String clave1,String clave2,Rol rol) throws ErrorServiceException {
      try {		
    	  
    	mecanico.setEliminado(false);
    	validar(BaseUseCaseService.ALTA, mecanico);
        Usuario user = usuarioService.alta(nombre, clave1, clave2,rol);
        mecanico.setUsuario(user);
        
        Mecanico guardado = repository.save(mecanico);
        return guardado;
        
      
        
      }catch(ErrorServiceException e) {
    	throw e; 
      }catch(Exception e) {
      	throw new ErrorServiceException("Error de Sistemas");  
      }   
    }


    @Transactional
    public Mecanico modificar(Long idMecanico,Mecanico mecanico,String nombre,String clave1,String clave2,Rol rol) throws ErrorServiceException {
      try {		
    	Mecanico newMecanico = repository.findById(idMecanico).orElseThrow(() -> new ErrorServiceException("Mecanico no encontrado"));
    	validar(BaseUseCaseService.MODIFICACION, mecanico);
        Usuario user = usuarioService.modificar(mecanico.getUsuario().getId(),nombre, clave1, clave2,rol);
        newMecanico.setUsuario(user);
        newMecanico.setNombre(mecanico.getNombre());
        newMecanico.setApellido(mecanico.getApellido());
        newMecanico.setLegajo(mecanico.getLegajo());
        Mecanico guardado = repository.save(newMecanico);
        
        return guardado;
        
      }catch(ErrorServiceException e) {
    	throw e; 
      }catch(Exception e) {
      	throw new ErrorServiceException("Error de Sistemas");  
      }   
    }

    @Transactional
    public void eliminar(Long idMecanico) throws ErrorServiceException{
        try{
            Mecanico mecanico = repository.findById(idMecanico).orElseThrow(() -> new ErrorServiceException("Mecanico no encontrado"));
            usuarioService.eliminar(mecanico.getUsuario().getId());
            mecanico.setEliminado(true);
            repository.save(mecanico);

        }catch(ErrorServiceException e) {
            throw e; 
        }catch(Exception e) {
            throw new ErrorServiceException("Error de Sistemas");  
        }   
    }

    public List<Mecanico> listarActivos() throws ErrorServiceException{
      try {	
    	  
        return repository.findAll().stream()
                         .filter(e -> !Boolean.TRUE.equals(e.getEliminado()))
                         .toList(); 
        
	  }catch(Exception e) {
		throw new ErrorServiceException("Error de Sistemas");  
	  }  
    }

    public Mecanico obtenerMecanico(Long idMecanico) throws ErrorServiceException{
        try{
            Optional<Mecanico> respuesta = repository.findById(idMecanico);
            if (respuesta.isPresent()) {
                Mecanico mecanico= respuesta.get();
                return mecanico;
            } else {
                throw new ErrorServiceException("No se encontró el usuario solicitado");
            }
        }catch(ErrorServiceException e) {
            throw e; 
        }catch(Exception e) {
            throw new ErrorServiceException("Error de Sistemas");  
        }  
    }

     public Mecanico obtenerMecanicoPorUser(Long idUsuario) throws ErrorServiceException{
        try{
            Mecanico respuesta = repository.buscarMecanicoPorUser(idUsuario);
            if (respuesta!=null) {
                return respuesta;
            } else {
                throw new ErrorServiceException("No se encontró el usuario solicitado");
            }
        }catch(ErrorServiceException e) {
            throw e; 
        }catch(Exception e) {
            throw new ErrorServiceException("Error de Sistemas");  
        }  
    }


    protected void validar(BaseUseCaseService useCase, Mecanico mecanico) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {

                if (mecanico == null) {
                    throw new ErrorServiceException("Debe indicar un Mecanico");
                }

                if (mecanico.getLegajo() == null || mecanico.getLegajo().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar un legajo");
                }
                if (mecanico.getNombre() == null || mecanico.getNombre().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el nombre");
                }
                if (mecanico.getApellido() == null || mecanico.getApellido().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el apellido");
                }

                

                Mecanico mecanicoExistente = ((MecanicoRepository) repository).buscarMecanicoPorLegajo(mecanico.getLegajo());
                boolean mecanicoActivo = mecanicoExistente != null && !mecanicoExistente.getEliminado();

                if (mecanicoActivo && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("Existe un mecanico con el legajo indicado");
                }

                if (mecanicoActivo && useCase == BaseUseCaseService.MODIFICACION
                        && !mecanicoExistente.getId().equals(mecanico.getId())) {
                    throw new ErrorServiceException("Existe un mecanico con el legajo indicado");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}

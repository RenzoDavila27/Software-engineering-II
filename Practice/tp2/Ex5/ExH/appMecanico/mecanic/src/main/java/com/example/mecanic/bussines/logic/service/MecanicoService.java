
/* 
package com.example.mecanic.bussines.logic.service;

import com.example.mecanic.bussines.domain.entity.Mecanico;
import com.example.mecanic.bussines.domain.entity.Vehiculo;
import com.example.mecanic.bussines.persistence.repository.MecanicoRepository;

import com.example.mecanic.bussines.logic.error.ErrorServiceException;
import com.example.mecanic.bussines.domain.enumeration.Rol;
import org.springframework.stereotype.Service;

@Service
public class MecanicoService extends BaseService<Mecanico, Long> {
    
    @Autowired
    private UsuarioService usuarioService;

    public MecanicoService(MecanicoRepository repository) {
        super(repository);
    }



    @Override
    public Mecanico alta(Mecanico mecanico,String nombre,String clave1,String clave2) throws ErrorServiceException {
      try {		
    	  
    	mecanico.setEliminado(false);
    	validar(BaseUseCaseService.ALTA, mecanico);
        usuarioService.validar(nombre,clave1,clave2);
        Usuario user = usuarioService.alta(nombre, clave1, clave2);
        mecanico.setUsuario(user);
        
        T guardado = repository.save(mecanico);
        
        return guardado;
        
      }catch(ErrorServiceException e) {
    	throw e; 
      }catch(Exception e) {
      	throw new ErrorServiceException("Error de Sistemas");  
      }   
    }


    @Override
    public Mecanico modificar(Long idMecanico,Mecanico mecanico,String nombre,String clave1,String clave2) throws ErrorServiceException {
      try {		
    	Mecanico newMecanico = repository.findById(idMecanico);
    	validar(BaseUseCaseService.MODIFICACION, mecanico);
        usuarioService.validar(nombre,clave1,clave2);
        Usuario user = usuarioService.modificar(mecanico.getUsuario().getId(),nombre, clave1, clave2);
        newMecanico.setUsuario(user);
        newMecanico.setNombre(mecanico.getNombre());
        newMecanico.setApellido(mecanico.getApellido());
        newMecanico.setLegajo(mecanico.getLegajo());
        T guardado = repository.save(newMecanico);
        
        return guardado;
        
      }catch(ErrorServiceException e) {
    	throw e; 
      }catch(Exception e) {
      	throw new ErrorServiceException("Error de Sistemas");  
      }   
    }


    @Override
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
*/
package com.example.mecanic.bussines.logic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; 
import com.example.mecanic.bussines.domain.entity.HistorialArreglo; 
import com.example.mecanic.bussines.domain.entity.Mecanico; 

import com.example.mecanic.bussines.logic.error.ErrorServiceException; 
import com.example.mecanic.bussines.logic.service.MecanicoService; 
import com.example.mecanic.bussines.logic.service.BaseUseCaseService; 

import com.example.mecanic.bussines.persistence.repository.HistorialArregloRepository; 


@Service
public class HistorialArregloService {

    @Autowired 
    private HistorialArregloRepository repository;

    @Autowired
    private MecanicoService mecanicoService;

    @Transactional
    public HistorialArreglo alta(HistorialArreglo historial, Long idUsuario) throws ErrorServiceException{
        try {		
    	  
    	historial.setEliminado(false);
    	validar(BaseUseCaseService.ALTA, historial);
        Mecanico mecanico = mecanicoService.obtenerMecanicoPorUser(idUsuario);
        historial.setMecanico(mecanico);
        
        HistorialArreglo guardado = repository.save(historial);
        return guardado;

      }catch(ErrorServiceException e) {
    	throw e; 
      }catch(Exception e) {
      	throw new ErrorServiceException("Error de Sistemas");  
      }   
    }

    @Transactional
    public HistorialArreglo modificar(Long idHistorial, HistorialArreglo historial,Long idUsuario) throws ErrorServiceException{
        try {		
    	HistorialArreglo newHistorial = repository.findById(idHistorial).orElseThrow(() -> new ErrorServiceException("Historial no encontrado"));
    	validar(BaseUseCaseService.MODIFICACION, historial);
        Mecanico mecanico = mecanicoService.obtenerMecanicoPorUser(idUsuario);
        newHistorial.setDetalleArreglo(historial.getDetalleArreglo());
        newHistorial.setFechaArreglo(historial.getFechaArreglo());
        newHistorial.setMecanico(mecanico);
        HistorialArreglo guardado = repository.save(newHistorial);
        
        return guardado;
        
      }catch(ErrorServiceException e) {
    	throw e; 
      }catch(Exception e) {
      	throw new ErrorServiceException("Error de Sistemas");  
      }   
    }

    public HistorialArreglo obtenerHistorial(Long idHistorial) throws ErrorServiceException {
        try{
            Optional<HistorialArreglo> respuesta = repository.findById(idHistorial);
            if (respuesta.isPresent()) {
                HistorialArreglo historial= respuesta.get();
                return historial;
            } else {
                throw new ErrorServiceException("No se encontró el usuario solicitado");
            }
        }catch(ErrorServiceException e) {
            throw e; 
        }catch(Exception e) {
            throw new ErrorServiceException("Error de Sistemas");  
        }  
    }

    @Transactional
    public Long eliminar(Long idHistorial) throws ErrorServiceException{
        try{
            HistorialArreglo historial = repository.findById(idHistorial).orElseThrow(() -> new ErrorServiceException("Historial no encontrado"));
            historial.setEliminado(true);
            repository.save(historial);
            return historial.getVehiculo().getId();

        }catch(ErrorServiceException e) {
            throw e; 
        }catch(Exception e) {
            throw new ErrorServiceException("Error de Sistemas");  
        }   
    }

    public List<HistorialArreglo> listarActivos(Long idVehiculo) throws ErrorServiceException{
        try{
            return repository.buscarHistorialArregloPorIdVehiculo(idVehiculo);

        }catch(Exception e) {
            throw new ErrorServiceException("Error de Sistemas");  
        } 
    }




    protected void validar(BaseUseCaseService useCase, HistorialArreglo historial) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {

                if (historial == null) {
                    throw new ErrorServiceException("Debe rellenar el historial");
                }
                if (historial.getVehiculo()==null) {
                    throw new ErrorServiceException("Este historial no tiene ningun vehiculo asociado");
                    
                }

                if (historial.getDetalleArreglo()==null || historial.getDetalleArreglo().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar un detalle");
                    
                }
                if (historial.getFechaArreglo()==null) {
                    throw new ErrorServiceException("Debe indicar una fecha de arreglo");   
                }
            }

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
    
}

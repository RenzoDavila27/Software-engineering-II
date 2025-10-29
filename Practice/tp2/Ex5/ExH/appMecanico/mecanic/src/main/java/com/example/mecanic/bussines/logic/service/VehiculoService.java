package com.example.mecanic.bussines.logic.service;

import com.example.mecanic.bussines.domain.entity.Vehiculo;
import com.example.mecanic.bussines.persistence.repository.VehiculoRepository;
import com.example.mecanic.bussines.logic.error.ErrorServiceException;
import com.example.mecanic.bussines.domain.enumeration.Rol;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService extends BaseService<Vehiculo, Long> {
    
    public VehiculoService(VehiculoRepository repository) {
        super(repository);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Vehiculo vehiculo) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {

                if (vehiculo == null) {
                    throw new ErrorServiceException("Debe indicar el vehículo");
                }

                if (vehiculo.getMarca() == null || vehiculo.getMarca().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar la marca del vehículo");
                }
                if (vehiculo.getModelo() == null || vehiculo.getModelo().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el modelo del vehículo");
                }
                if (vehiculo.getPatente() == null || vehiculo.getPatente().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar la patente del vehículo");
                }
                Vehiculo vehiculoExistente = ((VehiculoRepository) repository).buscarVehiculoPorPatente(vehiculo.getPatente());
                boolean vehiculoActivo = vehiculoExistente != null && !vehiculoExistente.getEliminado();

                if (vehiculoActivo && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("Existe un vehiculo con la patente indicada");
                }

                if (vehiculoActivo && useCase == BaseUseCaseService.MODIFICACION
                        && !vehiculoExistente.getId().equals(vehiculo.getId())) {
                    throw new ErrorServiceException("Existe un vehiculo con la patente indicada");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}
    
    
    




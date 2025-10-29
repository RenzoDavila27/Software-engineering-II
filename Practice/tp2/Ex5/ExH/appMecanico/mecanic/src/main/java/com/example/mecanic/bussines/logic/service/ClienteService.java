package com.example.mecanic.bussines.logic.service;

import com.example.mecanic.bussines.domain.entity.Cliente;

import com.example.mecanic.bussines.logic.error.ErrorServiceException;
import com.example.mecanic.bussines.persistence.repository.ClienteRepository;
import org.springframework.stereotype.Service;

@Service
public class ClienteService extends BaseService<Cliente,Long> {
    


    public ClienteService(ClienteRepository repository) {
        super(repository);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Cliente cliente) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {

                if (cliente == null) {
                    throw new ErrorServiceException("Debe indicar un cliente");
                }

                if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el nombre");
                }
                if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el apellido");
                }
                if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el documento");
                }
                Cliente clienteExistente = ((ClienteRepository) repository).buscarClientePorDocumento(cliente.getDocumento());
                boolean clienteActivo = clienteExistente != null && !clienteExistente.getEliminado();

                if (clienteActivo && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("Existe un cliente con el documento indicado");
                }

                if (clienteActivo && useCase == BaseUseCaseService.MODIFICACION
                        && !clienteExistente.getId().equals(cliente.getId())) {
                    throw new ErrorServiceException("Existe un cliente con el documento indicado");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}

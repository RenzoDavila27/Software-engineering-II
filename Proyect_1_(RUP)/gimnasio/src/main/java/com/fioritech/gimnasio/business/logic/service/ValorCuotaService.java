package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.ValorCuota;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.ValorCuotaRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ValorCuotaService {

    @Autowired
    private ValorCuotaRepository repository;

    @Transactional
    public void crearValorCuota(Double valor, Date fechadesde) {
        try {
            validar(valor);

            ValorCuota valorcuota = repository.valorcuotaActual();

            
            if (valorcuota == null) {
                ValorCuota nuevo = new ValorCuota();
                nuevo.setValorCuota(valor);
                nuevo.setFechaDesde(new Date());
                repository.save(nuevo);

            
            } else if (valorcuota.getValorCuota() == valor && valorcuota.isEliminado()!=false && valorcuota.getFechaHasta()==null) {
                throw new BusinessException("El valor de la nueva cuota coincide con la última");

           
            } else {
                Date fecha = new Date();
                LocalDate fechaHasta = fecha.toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                                .minusDays(1);
                valorcuota.setFechaHasta(Date.from(fechaHasta.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                repository.save(valorcuota);

                
                ValorCuota valorcuotanew = new ValorCuota();
                valorcuotanew.setValorCuota(valor);
                valorcuotanew.setFechaDesde(new Date());
                repository.save(valorcuotanew);
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("Error inesperado del Sistema");
        }
    }

    public void validar(Double valor)  {
        if (valor == null) {
            throw new BusinessException("Ingrese un valor válido");
        }
    }


    @Transactional
    public void modificarValorCuota(String id, Double valor){
        try{
            if (valor==null) {
                throw new BusinessException("Ingrese un valor");
            }
            ValorCuota valorCuota = repository.buscarValorCuotaPorId(id);
            if (valorCuota==null){
                throw new BusinessException("No se encontro la Cuota");
            }
            if (valorCuota.getValorCuota() == valor) {
                throw new BusinessException("La Cuota ya tiene ese valor");
            }
            valorCuota.setValorCuota(valor);
            repository.save(valorCuota);
        }catch(BusinessException e){
            throw e;
        }catch(Exception ex){
            ex.printStackTrace();
            throw new BusinessException("Error inesperado de Sistema");
        }
    }

    
    @Transactional
    public void eliminarValorCuota(String id){
        try {
            ValorCuota valorcuota = repository.buscarValorCuotaPorId(id);

            if (valorcuota == null) {
                throw new BusinessException("No se encontró la cuota indicada");
            }

            if (!valorcuota.isEliminado() && valorcuota.getFechaHasta() == null) {
                
                valorcuota.setEliminado(true);
                repository.save(valorcuota);

                
                ValorCuota valorcuotaold = repository.buscarPenultimaValorCuota();
                if (valorcuotaold != null) {
                    valorcuotaold.setFechaHasta(null);
                    repository.save(valorcuotaold);
                }
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("Error inesperado del sistema");
        }
    }

    public Collection<ValorCuota> listarValorCuota() {

        try{
            Collection<ValorCuota> cuotas = repository.listarValorCuota();
            return cuotas;
        }catch(Exception ex){
            ex.printStackTrace();
            throw new BusinessException("Error inesperado de Sistema");
        }

    }

    public Collection<ValorCuota> listarValorCuotaActivas(){

        try{
            Collection<ValorCuota> cuotas = repository.listarValorCuotasActivas();
            return cuotas;
        }catch(Exception ex){
            ex.printStackTrace();
            throw new BusinessException("Error inesperado de Sistema");
        }

    }

    public Double valorcuotaActual(){
        try{
            ValorCuota cuota_actual = repository.valorcuotaActual();
            if (cuota_actual==null) {
                throw new BusinessException("No hay ningun valor de cuota disponible");
                
            }else{
                return cuota_actual.getValorCuota();
            }
        }catch(BusinessException e){
            throw e;
        }catch(Exception ex){
            ex.printStackTrace();
            throw new BusinessException("Error inesperado del sistema");
        }
    }

    public ValorCuota ultimaCuota() {
        try{
            ValorCuota valor = repository.buscarultimaCuota();
            return valor;
        }catch(Exception e){
            e.printStackTrace();
            throw new BusinessException("Error inesperado de Sistema");
        }
    }

    public ValorCuota buscarValorCuota(String id) {
        try{
            ValorCuota valor = repository.buscarValorCuotaPorId(id);
            if (valor==null) {
                throw new BusinessException("No se encontro la cuota");
            }
            return valor;
        }catch(BusinessException e){
            throw e;
        }catch(Exception ex){
            ex.printStackTrace();
            throw new BusinessException("Error inesperado de Sistema");
        }
    }

    
}


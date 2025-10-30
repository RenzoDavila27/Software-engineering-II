package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.CuotaMensual;
import com.fioritech.gimnasio.business.domain.Promocion;

import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.EstadoCuotaMensual;

import com.fioritech.gimnasio.business.domain.enums.TipoMensaje;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.PromocionRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PromocionService {

    private final PromocionRepository promocionRepository;
    private final UsuarioService usuarioService;

    public PromocionService(PromocionRepository promocionRepository, UsuarioService usuarioService) {
        this.promocionRepository = promocionRepository;
        this.usuarioService = usuarioService;
    }
    @Autowired
    private EmailService emailService;

     @Autowired
    private SocioService socioService;

    @Autowired
    private CuotaMensualService CuotaMensualService;

    @Transactional
    public Promocion crearPromocion(String idUsuario, Date fechaPromocion, String titulo, String texto,TipoMensaje tipoMensaje) {
        validar(idUsuario, fechaPromocion, titulo, texto);
        Usuario usuario = usuarioService.buscarUsuario(idUsuario);
        Promocion promocion = new Promocion();
        promocion.setUsuario(usuario);
        promocion.setFechaEnvioPromocion(fechaPromocion);
        promocion.setTitulo(titulo.trim());
        promocion.setTexto(texto.trim());
        promocion.setTipoMensaje(tipoMensaje);
        promocion.setCantidadSociosEnviados(0L);
        promocion.setEliminado(false);
        System.out.println("CREE LA PROMOCION");
        return promocionRepository.save(promocion);
    }

    public void validar(String idUsuario, Date fechaPromocion, String titulo, String texto) {
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new BusinessException("El usuario es obligatorio");
        }
        if (fechaPromocion == null) {
            throw new BusinessException("La fecha de envio es obligatoria");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new BusinessException("El titulo es obligatorio");
        }
        if (texto == null || texto.isBlank()) {
            throw new BusinessException("El texto es obligatorio");
        }
    }

    @Transactional(readOnly = true)
    public Promocion buscarPromocion(String id) {
        return promocionRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Promocion no encontrada"));
    }

    public Promocion modificarPromocion(String id, String idUsuario, Date fechaPromocion, String titulo,String texto,TipoMensaje tipoMensaje) {
        Promocion promocion = buscarPromocion(id);
        if (idUsuario != null && !idUsuario.isBlank()) {
            promocion.setUsuario(usuarioService.buscarUsuario(idUsuario));
        }
        if (fechaPromocion != null) {
            promocion.setFechaEnvioPromocion(fechaPromocion);
        }
        if (titulo != null && !titulo.isBlank()) {
            promocion.setTitulo(titulo.trim());
        }
        if (texto != null && !texto.isBlank()) {
            promocion.setTexto(texto.trim());
        }
         if (tipoMensaje != null) {
            promocion.setTipoMensaje(tipoMensaje);
        }
        return promocionRepository.save(promocion);
    }

    @Transactional
    public void eliminarPromocion(String id) {
        Promocion promocion= buscarPromocion(id);
        promocion.setEliminado(true);
        promocionRepository.save(promocion);
    }

    @Transactional(readOnly = true)
    public List<Promocion> listarPromocion() {
        return promocionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Promocion> listarPromocionActivo() {
        return promocionRepository.findAll().stream()
            .filter(p -> !p.isEliminado())
            .toList();
    }

    @Transactional
    public void enviarMensaje(String idPromocion){
        System.out.println("ENTRE AL SISTEMA DE ENVIO DE PROMOCION");
        try{
          Promocion promocion = buscarPromocion(idPromocion);	
          if(promocion==null){
              throw new BusinessException("El mensaje no existe");
          }
          if(promocion.isEliminado()){
              throw new BusinessException("EL mensaje esta eliminado");
          }
           
            if (promocion.getTipoMensaje() == TipoMensaje.CUMPLEANOS){
                Collection<Socio> listaSociosCumpleaños = socioService.listarCumpleanieros();
                enviarCumpleanios(listaSociosCumpleaños,promocion);

            }else if(promocion.getTipoMensaje() == TipoMensaje.PROMOCION){;
                Collection<Socio> listaSocios = socioService.listarSociosActivos();
                enviarPromocion(listaSocios,promocion);
                
            }else if (promocion.getTipoMensaje() == TipoMensaje.DEUDA){
                Collection<Socio> listaSocios = socioService.SocioConDeudas(EstadoCuotaMensual.ADEUDADA);
                enviarDeuda(listaSocios,promocion);
                
            }
        }catch(BusinessException e) {	
            throw e;
        }catch(Exception ex){
            ex.printStackTrace();
            throw new BusinessException("Error desconocido");
        }
    }
    public void enviarCumpleanios(Collection<Socio> listaSociosCumpleaños,Promocion promocion){
        for(Socio socio: listaSociosCumpleaños){
           emailService.sendEmail(socio.getCorreoElectronico(), promocion.getTitulo(), promocion.getTexto());
        }
        promocion.setCantidadSociosEnviados((long) listaSociosCumpleaños.size() + promocion.getCantidadSociosEnviados());
        promocionRepository.save(promocion);
    }

    public void enviarPromocion(Collection<Socio> listaSocios,Promocion promocion){
        for(Socio socio: listaSocios){
           emailService.sendEmail(socio.getCorreoElectronico(), promocion.getTitulo(), promocion.getTexto());
        }
        promocion.setCantidadSociosEnviados((long) listaSocios.size() + promocion.getCantidadSociosEnviados());
        promocionRepository.save(promocion);
    }

    public void enviarDeuda(Collection<Socio> listaSocios,Promocion promocion){
        for(Socio socio: listaSocios){
            Collection<CuotaMensual> cuotas = CuotaMensualService.listarDeudasPorSocio(socio.getId(), EstadoCuotaMensual.ADEUDADA);
            String texto = promocion.getTexto() + "\nDetalle de la deuda:\n";
            double deudatotal = 0;
           for(CuotaMensual cuota: cuotas){
                 texto = texto
                    + "Mes: " + cuota.getMes() + "\n"
                    + "Año: " + cuota.getAnio() + "\n"
                    + "Estado: " + cuota.getEstado() + "\n\n";
                deudatotal = deudatotal + cuota.getValorCuota().getValorCuota();

            }
                texto = texto + "Deuda total: " + String.valueOf(deudatotal);
                emailService.sendEmail(socio.getCorreoElectronico(), promocion.getTitulo(), texto);
            }
           
        promocion.setCantidadSociosEnviados((long) listaSocios.size() + promocion.getCantidadSociosEnviados());
        promocionRepository.save(promocion);
    }

}

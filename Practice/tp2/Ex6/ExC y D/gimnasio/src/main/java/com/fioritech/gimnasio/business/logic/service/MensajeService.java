package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.CuotaMensual;
import com.fioritech.gimnasio.business.domain.Mensaje;
import com.fioritech.gimnasio.business.domain.Promocion;
import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.EstadoCuotaMensual;
import com.fioritech.gimnasio.business.domain.enums.TipoMensaje;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.MensajeRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioService usuarioService;

    public MensajeService(MensajeRepository mensajeRepository, UsuarioService usuarioService) {
        this.mensajeRepository = mensajeRepository;
        this.usuarioService = usuarioService;
    }
    @Autowired
    private EmailService emailService;

    @Autowired
    private CuotaMensualService cuotaMensualService;

    @Autowired
    private SocioService socioService;

    public Mensaje crearMensaje(String idUsuario, String titulo, String texto, TipoMensaje tipoMensaje) {
        validar(idUsuario, titulo, texto, tipoMensaje);
        Usuario usuario = usuarioService.buscarUsuario(idUsuario);
        Mensaje mensaje = new Mensaje();
        mensaje.setUsuario(usuario);
        mensaje.setTitulo(titulo.trim());
        mensaje.setTexto(texto.trim());
        mensaje.setTipoMensaje(tipoMensaje);
        return mensajeRepository.save(mensaje);
    }

    public void validar(String idUsuario, String titulo, String texto, TipoMensaje tipoMensaje) {
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new BusinessException("El usuario es obligatorio");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new BusinessException("El titulo es obligatorio");
        }
        if (texto == null || texto.isBlank()) {
            throw new BusinessException("El texto es obligatorio");
        }
        if (tipoMensaje == null) {
            throw new BusinessException("El tipo de mensaje es obligatorio");
        }
    }

    public Mensaje modificarMensaje(String id, String idUsuario, String titulo, String texto, TipoMensaje tipoMensaje) {
        Mensaje mensaje = buscarMensaje(id);
        if (idUsuario != null && !idUsuario.isBlank()) {
            mensaje.setUsuario(usuarioService.buscarUsuario(idUsuario));
        }
        if (titulo != null && !titulo.isBlank()) {
            mensaje.setTitulo(titulo.trim());
        }
        if (texto != null && !texto.isBlank()) {
            mensaje.setTexto(texto.trim());
        }
        if (tipoMensaje != null) {
            mensaje.setTipoMensaje(tipoMensaje);
        }
        return mensajeRepository.save(mensaje);
    }

    public void eliminarMensaje(String id) {
        Mensaje mensaje = buscarMensaje(id);
        mensaje.setEliminado(true);
        mensajeRepository.save(mensaje);
    }

    @Transactional(readOnly = true)
    public Mensaje buscarMensaje(String id) {
        return mensajeRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Mensaje no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Mensaje> listarMensaje() {
        return mensajeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Mensaje> listarMensajeActivo(){
        List<Mensaje> mensajes =  mensajeRepository.listarMensajeActivo();
        return mensajes;
    }

    public void enviarMensaje(String idMensaje) {
       
        try{
          Mensaje mensaje = buscarMensaje(idMensaje);	
          if(mensaje==null){
              throw new BusinessException("El mensaje no existe");
          }
          if(mensaje.isEliminado()){
              throw new BusinessException("EL mensaje esta eliminado");
          }
           
            if (mensaje.getTipoMensaje() == TipoMensaje.CUMPLEANOS){
                Collection<Socio> listaSociosCumpleaños = socioService.listarCumpleanieros();
                enviarCumpleanios(listaSociosCumpleaños,mensaje);

            }else if(mensaje.getTipoMensaje() == TipoMensaje.PROMOCION){;
                Collection<Socio> listaSocios = socioService.listarSociosActivos();
                enviarPromocion(listaSocios,mensaje);
                
            }else if (mensaje.getTipoMensaje() == TipoMensaje.DEUDA){
                Collection<Socio> listaSocios = socioService.SocioConDeudas(EstadoCuotaMensual.ADEUDADA);
                enviarDeuda(listaSocios,mensaje);
                
            }
        }catch(BusinessException e) {	
            throw e;
        }catch(Exception ex){
            ex.printStackTrace();
            throw new BusinessException("Error desconocido");
        }
        
    }

    public void enviarCumpleanios(Collection<Socio> listaSociosCumpleaños,Mensaje mensaje){
        for(Socio socio: listaSociosCumpleaños){
           emailService.sendEmail(socio.getCorreoElectronico(), mensaje.getTitulo(), mensaje.getTexto());
        }
    }

    public void enviarPromocion(Collection<Socio> listaSocios,Mensaje mensaje){
        for(Socio socio: listaSocios){
           emailService.sendEmail(socio.getCorreoElectronico(), mensaje.getTitulo(), mensaje.getTexto());
        }
    }

    public void enviarDeuda(Collection<Socio> listaSocios,Mensaje mensaje){
        for(Socio socio: listaSocios){
            Collection<CuotaMensual> cuotas = cuotaMensualService.listarDeudasPorSocio(socio.getId(), EstadoCuotaMensual.ADEUDADA);
            String texto = mensaje.getTexto() + "\nDetalle de la deuda:\n";
            double deudatotal = 0;
           for(CuotaMensual cuota: cuotas){
                 texto = texto
                    + "Mes: " + cuota.getMes() + "\n"
                    + "Año: " + cuota.getAnio() + "\n"
                    + "Estado: " + cuota.getEstado() + "\n\n";
                deudatotal = deudatotal + cuota.getValorCuota().getValorCuota();

            }
                texto = texto + "Deuda total: " + String.valueOf(deudatotal);
                emailService.sendEmail(socio.getCorreoElectronico(), mensaje.getTitulo(), texto);
            }
        
    }
}

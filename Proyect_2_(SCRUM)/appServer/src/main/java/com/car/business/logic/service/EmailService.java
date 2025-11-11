/* 
package com.car.business.logic.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.car.business.domain.Alquiler;

@Service
public class EmailService {
    
    private final JavaMailSender emailSender;

    public EmailService(final JavaMailSender emailSender){
        this.emailSender =emailSender;
    }

    public void sendEmail(Alquiler a){
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setFrom("bdfioritech@gmail.com");
        msg.setTo(a.getCliente().getContacto().getContactoCorreoElectronico().getEmail());
        msg.setSubject("Recordatorio: devolución del vehículo mañana");
        msg.setText("""
                Hola %s,

                Te recordamos que la devolución de tu vehículo es el %s.

                ¡Gracias!
                """.formatted(
                a.getCliente().getNombre() + ' ' + a.getCliente().getApellido() + ' ' + a.getCliente().getNumeroDocumento(),
                a.getFechaHasta()
        ));
        emailSender.send(msg);
    }
    
}
*/

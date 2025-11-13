package com.car.business.logic.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;


import com.car.business.domain.Alquiler;
import com.car.business.domain.Contacto;
import com.car.business.domain.ContactoCorreoElectronico;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(final JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(Alquiler a) {
        for (Contacto c : a.getCliente().getContactos()) {
            if (c instanceof ContactoCorreoElectronico contactoCorreo) {
                try {
                    MimeMessage message = emailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                    helper.setFrom("bdfioritech@gmail.com");
                    helper.setTo(contactoCorreo.getEmail());
                    helper.setSubject("Recordatorio: devolución del vehículo mañana");

                    String html = """
                        <html>
                          <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <h2 style="color: #2C3E50;">Recordatorio de devolución del vehículo</h2>
                            <p>Hola <b>%s %s (%s)</b>,</p>

                            <p>Te recordamos que la devolución de tu vehículo es el <b>%s</b>.</p>

                            <table style="border-collapse: collapse; margin-top: 10px;">
                              <tr><td><b>Modelo:</b></td><td>%s</td></tr>
                              <tr><td><b>Marca:</b></td><td>%s</td></tr>
                              <tr><td><b>Patente:</b></td><td>%s</td></tr>
                            </table>

                            <p>¡Gracias por confiar en nosotros! 🚗</p>

                            <hr>
                            <small style="color: #888;">FioriTech RentCar © 2025</small>
                          </body>
                        </html>
                        """.formatted(
                            a.getCliente().getNombre(),
                            a.getCliente().getApellido(),
                            a.getCliente().getNumeroDocumento(),
                            a.getFechaHasta(),
                            a.getVehiculo().getCaracteristicaVehiculo().getModelo(),
                            a.getVehiculo().getCaracteristicaVehiculo().getMarca(),
                            a.getVehiculo().getPatente()
                        );

                    helper.setText(html, true); // 👈 true indica que es HTML

                    emailSender.send(message);
                } catch (MessagingException e) {
                    throw new RuntimeException("Error al enviar el correo HTML", e);
                }
            }
        }
    }
}

package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.template.OperationTemplateService;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService extends OperationTemplateService<EmailService.EmailData, Void> {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String content) {
        ejecutar(new EmailData(to, subject, content));
    }

    public boolean enviarCorreoHtml(String to, String subject, String content) {
        ejecutar(new EmailData(to, subject, content));
        return true;
    }

    @Override
    protected void validarEntrada(EmailData data) {
        if (ValidationUtils.isBlank(data.to())) {
            throw new BusinessException("El destinatario es obligatorio");
        }
        if (ValidationUtils.isBlank(data.subject())) {
            throw new BusinessException("El asunto es obligatorio");
        }
        if (ValidationUtils.isBlank(data.content())) {
            throw new BusinessException("El contenido es obligatorio");
        }
    }

    @Override
    protected Void ejecutarOperacion(EmailData data) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(data.to());
            helper.setSubject(data.subject());
            helper.setText(data.content(), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BusinessException("No se pudo enviar el email", e);
        }
        return null;
    }

    protected record EmailData(String to, String subject, String content) {}
}


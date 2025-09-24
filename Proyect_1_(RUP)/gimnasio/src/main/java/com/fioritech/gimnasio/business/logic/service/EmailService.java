package com.fioritech.gimnasio.business.logic.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private final JavaMailSender emailSender;

    public EmailService(final JavaMailSender emailSender){
        this.emailSender =emailSender;
    }

    public void sendEmail(String to , String subject, String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom("bdfioritech@gmail.com");
        emailSender.send(message);
    }
    
}

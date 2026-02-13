package com.localapp.mgmt.userprofile.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * service to send email to the user/travel agent/admin
 */
@Service
public class EmailService {
    @Value("${from.email.address}")
    private String fromEmailAddress;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * sends email
     *
     * @param recipient - user/travel agent/email
     * @param subject   - subject of email
     * @param content   - content of email
     * @throws MessagingException - exception thrown when message cannot be sent to email
     */
    @Async
    public void sendEmail(String recipient, String subject, String content)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromEmailAddress);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }
}
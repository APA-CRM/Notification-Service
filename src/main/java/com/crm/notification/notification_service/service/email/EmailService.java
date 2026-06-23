package com.crm.notification.notification_service.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(List<String> to, String text, String subject) {
        for (String email : to) {
            sendEmail(email, text, subject);
        }
    }

    public void sendEmail(String to, String text, String subject) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(text, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Something went wrong while sending email to {} with subject {}", to, subject, e);
            throw new RuntimeException(e);
        }


        log.debug("Email successfully sent to {} with subject {}", to, subject);
    }
}

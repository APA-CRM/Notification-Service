package com.crm.notification.notification_service.service.consumer;


import com.crm.notification.notification_service.service.email.EmailService;
import com.crm.notification.notification_service.service.email.TemplateBuilder;
import com.crm.sharedlib.dto.amqp.SendPasswordByEmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.crm.sharedlib.consts.CrmConstants.SEND_PASSWORD_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordSenderConsumer {

    private final EmailService emailService;

    private final TemplateBuilder templateBuilder;

    @RabbitListener(queues = SEND_PASSWORD_QUEUE)
    public void sendPassword(SendPasswordByEmailEvent message) {
        log.info("Sending email with password to user");

        String subject = "🔐Your Generated Password!";
        String text = templateBuilder.buildPasswordEmail(message.getPassword());

        emailService.sendEmail(message.getEmail(), text, subject);

    }

}

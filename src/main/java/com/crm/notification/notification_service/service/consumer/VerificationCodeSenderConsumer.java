package com.crm.notification.notification_service.service.consumer;

import com.crm.notification.notification_service.service.email.EmailService;
import com.crm.notification.notification_service.service.email.TemplateBuilder;
import com.crm.sharedlib.messaging.dto.amqp.SendVerificationCodeByEmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.crm.sharedlib.messaging.constants.RabbitMQConstants.SEND_VERIFICATION_CODE_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeSenderConsumer {

    private static final String SUBJECT = "Verification Code";

    private final TemplateBuilder templateBuilder;
    private final EmailService emailService;

    @RabbitListener(queues = SEND_VERIFICATION_CODE_QUEUE)
    public void sendVerificationCode(SendVerificationCodeByEmailMessage message) {
        log.info("Sending email with verification code to user");

        String text = templateBuilder.buildVerificationCodeEmail(message.getVerificationCode());

        emailService.sendEmail(message.getEmail(), text, SUBJECT);
    }

}

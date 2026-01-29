package com.crm.notification.notification_service.consumer;

import com.crm.notification.notification_service.BaseIntegrationTest;
import com.crm.notification.notification_service.service.consumer.VerificationCodeSenderConsumer;
import com.crm.sharedlib.messaging.dto.amqp.SendVerificationCodeByEmailMessage;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

// TODO: Add RabbitMQ Testcontainers
class VerificationCodeSenderConsumerIntegrationTest extends BaseIntegrationTest {

    @MockitoSpyBean
    private JavaMailSender javaMailSender;

    @Autowired
    private VerificationCodeSenderConsumer consumer;

    @Test
    @DisplayName("Verification of the method when sending a verification code to user by email expected success")
    void sendVerificationCodeToUserExpectedSuccess() {
        SendVerificationCodeByEmailMessage message = new SendVerificationCodeByEmailMessage();
        message.setEmail("test@local");
        message.setVerificationCode("1234");

        Mockito.doNothing().when(javaMailSender)
                .send(Mockito.any(MimeMessage.class));

        Mockito.doReturn(Mockito.mock(MimeMessage.class)).when(javaMailSender)
                .createMimeMessage();

        consumer.sendVerificationCode(message);

        Mockito.verify(javaMailSender, Mockito.atLeastOnce()).send(Mockito.any(MimeMessage.class));
    }

}

package com.crm.notification.notification_service.consumer;

import com.crm.notification.notification_service.BaseIntegrationTest;
import com.crm.notification.notification_service.service.consumer.PasswordSenderConsumer;
import com.crm.sharedlib.messaging.dto.amqp.SendPasswordByEmailMessage;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

// TODO: Add RabbitMQ Testcontainers
class PasswordSenderConsumerIntegrationTest extends BaseIntegrationTest {

    @MockitoSpyBean
    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordSenderConsumer consumer;

    @Test
    @DisplayName("Verification of the method when sending a password to user by email expected success")
    void sendPasswordToUserExpectedSuccess() {
        SendPasswordByEmailMessage message = new SendPasswordByEmailMessage();
        message.setEmail("test@local");
        message.setPassword("123456");

        Mockito.doNothing().when(javaMailSender)
                .send(Mockito.any(MimeMessage.class));

        Mockito.doReturn(Mockito.mock(MimeMessage.class)).when(javaMailSender)
                .createMimeMessage();

        consumer.sendPassword(message);

        Mockito.verify(javaMailSender, Mockito.atLeastOnce()).send(Mockito.any(MimeMessage.class));
    }

}

package com.crm.notification.notification_service.consumer;

import com.crm.notification.notification_service.BaseIntegrationTest;
import com.crm.notification.notification_service.service.consumer.SendInvitationOfOrganizationConsumer;
import com.crm.sharedlib.messaging.dto.amqp.SendInvitationOfOrganizationMessage;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

// TODO: Add RabbitMQ Testcontainers
class SendInvitationOfOrganizationConsumerTest extends BaseIntegrationTest {

    @MockitoSpyBean
    private JavaMailSender javaMailSender;

    @Autowired
    private SendInvitationOfOrganizationConsumer consumer;

    @Test
    @DisplayName("Verification of the method when sending an invitation to user expected success")
    void sendInvitationOfOrganizationToUserThenSuccess() {
        SendInvitationOfOrganizationMessage event = new SendInvitationOfOrganizationMessage();

        event.setEmail("test@local");
        event.setOrganizationName("TestOrganization");
        event.setInvitationId(UUID.randomUUID());

        Mockito.doNothing().when(javaMailSender)
                .send(Mockito.any(MimeMessage.class));

        Mockito.doReturn(Mockito.mock(MimeMessage.class)).when(javaMailSender)
                .createMimeMessage();

        consumer.sendInvitationOfOrganizationToUser(event);

        Mockito.verify(javaMailSender, Mockito.atLeastOnce()).send(Mockito.any(MimeMessage.class));

    }

}
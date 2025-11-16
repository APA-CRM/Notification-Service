package com.crm.notification.notification_service.consumer;

import com.crm.notification.notification_service.BaseIntegrationTest;
import com.crm.notification.notification_service.service.consumer.SendInvitationOfOrganizationConsumer;
import com.crm.sharedlib.dto.amqp.SendInvitationOfOrganizationEvent;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

class SendInvitationOfOrganizationConsumerTest extends BaseIntegrationTest {

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    private SendInvitationOfOrganizationConsumer consumer;

    @Test
    @DisplayName("Verification of the method when sending an invitation to user expected success")
    void sendInvitationOfOrganizationToUserThenSuccess() {
        SendInvitationOfOrganizationEvent event = new SendInvitationOfOrganizationEvent();

        event.setEmail("test@local");
        event.setOrganizationName("TestOrganization");
        event.setInvitationId(UUID.randomUUID());

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));

        consumer.sendInvitationOfOrganizationToUser(event);

        Mockito.verify(javaMailSender, Mockito.atLeastOnce()).send(Mockito.any(MimeMessage.class));

    }

}
package com.crm.notification.notification_service.service.consumer;

import com.crm.notification.notification_service.service.email.EmailService;
import com.crm.notification.notification_service.service.email.TemplateBuilder;
import com.crm.sharedlib.messaging.dto.amqp.SendInvitationOfOrganizationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.crm.sharedlib.messaging.constants.RabbitMQConstants.SEND_INVITATION_OF_ORGANIZATION_ROUTING_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendInvitationOfOrganizationConsumer {

    private static final String SUBJECT = "Invitation to Join Organization";

    private final EmailService emailService;
    private final TemplateBuilder templateBuilder;

    @Value("${app.frontend.url}")
    private String frontEndUrl;

    @RabbitListener(queues = SEND_INVITATION_OF_ORGANIZATION_ROUTING_KEY)
    public void sendInvitationOfOrganizationToUser(SendInvitationOfOrganizationMessage event) {
        log.debug("Sending invitation {} to user", event.getInvitationId());

        String text = templateBuilder
                .buildInvitationEmail(
                        event.getInvitationId(),
                        event.getOrganizationName(), frontEndUrl
                );

        emailService.sendEmail(event.getEmail(), text, SUBJECT);
    }

}

package com.crm.notification.notification_service.service;

import com.crm.notification.notification_service.dto.response.NotificationResponse;
import com.crm.sharedlib.dto.CrmMessage;
import com.crm.sharedlib.dto.CrmNotification;
import com.crm.sharedlib.dto.CrmRecipient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSender {

    private final static String USER_NOTIFICATIONS_TOPIC = "/topic/notifications";
    private final static String ORGANIZATION_NOTIFICATIONS_TOPIC = "/topic/organizations/%d/notifications";

    private final SimpMessagingTemplate template;

    public void sendNotification(CrmNotification notification) {
        CrmRecipient recipient = notification.getRecipient();
        switch (recipient.getType()) {
            case USER -> sendMessageToUser(
                    recipient.getId(), notification.getMessage(),
                    notification.getDetails()
            );
            case ORGANIZATION -> sendMessageToOrganization(
                    recipient.getId(), notification.getMessage(),
                    notification.getDetails()
            );
            default -> throw new IllegalArgumentException("Unknow type of recipient " + recipient.getType());
        }
    }

    public void sendMessageToUser(
            Long userId, CrmMessage message,
            Map<String, Object> details
    ) {
        log.debug("Sending a message to user {} with message type {}", userId, message.getMessageType());

        NotificationResponse response =
                new NotificationResponse(message.getMessageType(), message.getMessage(), details);

        template.convertAndSendToUser(userId.toString(), USER_NOTIFICATIONS_TOPIC, response);
    }

    public void sendMessageToOrganization(
            Long organizationId, CrmMessage message,
            Map<String, Object> details
    ) {
        log.debug("Sending a message to organization {} with message type {}", organizationId, message.getMessageType());

        NotificationResponse response =
                new NotificationResponse(message.getMessageType(), message.getMessage(), details);

        template.convertAndSend(ORGANIZATION_NOTIFICATIONS_TOPIC.formatted(organizationId), response);
    }

}

package com.crm.notification.notification_service.service;

import com.crm.notification.notification_service.dto.response.NotificationResponse;
import com.crm.sharedlib.dto.CrmMessage;
import com.crm.sharedlib.dto.CrmNotification;
import com.crm.sharedlib.dto.CrmRecipient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
            case USER -> sendMessageToUser(recipient.getId(), notification.getMessage());
            case ORGANIZATION -> sendMessageToOrganization(recipient.getId(), notification.getMessage());
            default -> throw new IllegalArgumentException("Unknow type of recipient " + recipient.getType());
        }
    }

    public void sendMessageToUser(
            Long userId, CrmMessage message
    ) {
        log.debug("Sending a message to user {} with message code {}", userId, message.getCode());

        NotificationResponse response =
                new NotificationResponse(message.getCode(), message.getMessage(), message.getDetails());

        template.convertAndSendToUser(userId.toString(), USER_NOTIFICATIONS_TOPIC, response);
    }

    public void sendMessageToOrganization(
            Long organizationId, CrmMessage message
    ) {
        log.debug("Sending a message to organization {} with message code {}", organizationId, message.getCode());

        NotificationResponse response =
                new NotificationResponse(message.getCode(), message.getMessage(), message.getDetails());

        template.convertAndSend(ORGANIZATION_NOTIFICATIONS_TOPIC.formatted(organizationId), response);
    }

}

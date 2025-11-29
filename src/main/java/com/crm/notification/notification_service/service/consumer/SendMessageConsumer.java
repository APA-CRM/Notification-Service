package com.crm.notification.notification_service.service.consumer;

import com.crm.notification.notification_service.service.NotificationSender;
import com.crm.sharedlib.dto.CrmNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.crm.sharedlib.consts.CrmConstants.SEND_MESSAGE_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendMessageConsumer {

    private final NotificationSender notificationSender;

    @RabbitListener(queues = SEND_MESSAGE_QUEUE)
    public void sendMessage(CrmNotification notification) {
        log.debug("Notification received from the queue for a recipient {} with type {}",
                notification.getRecipient().getId(), notification.getRecipient().getType());

        try {
            notificationSender.sendNotification(notification);
        } catch (Exception e) {
            log.error("Error has occurred while sending notification", e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

}

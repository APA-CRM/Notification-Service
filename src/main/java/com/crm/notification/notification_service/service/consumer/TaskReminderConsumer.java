package com.crm.notification.notification_service.service.consumer;

import com.crm.notification.notification_service.service.email.EmailService;
import com.crm.notification.notification_service.service.email.TemplateBuilder;
import com.crm.sharedlib.messaging.dto.amqp.TaskReminderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.crm.sharedlib.messaging.constants.RabbitMQConstants.REMIND_ABOUT_TASK_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskReminderConsumer {

    private static final String SUBJECT = "Task reminder";

    private final EmailService emailService;
    private final TemplateBuilder templateBuilder;

    @Value("${app.frontend.url}")
    private String frontEndUrl;

    @RabbitListener(queues = REMIND_ABOUT_TASK_QUEUE)
    public void remindAboutTask(TaskReminderMessage message) {
        log.debug("Send email to remind about task {}", message.getTaskId());

        String text = templateBuilder.buildTaskReminderEmail(message, frontEndUrl);

        emailService.sendEmail(message.getEmails(), text, SUBJECT);
    }

}

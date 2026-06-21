package com.crm.notification.notification_service.consumer;

import com.crm.notification.notification_service.BaseIntegrationTest;
import com.crm.notification.notification_service.service.consumer.TaskReminderConsumer;
import com.crm.sharedlib.messaging.dto.amqp.TaskReminderMessage;
import com.crm.sharedlib.messaging.dto.amqp.TaskReminderMessage.TaskExtraInfo;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

class TaskReminderConsumerTest extends BaseIntegrationTest {

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    private TaskReminderConsumer consumer;

    @Test
    @DisplayName("Verification of the method when sending a verification code to user by email expected success")
    void sendVerificationCodeToUserExpectedSuccess() {
        TaskReminderMessage message = new TaskReminderMessage();
        message.setTaskId(UUID.randomUUID());
        message.setEmails(List.of("email1", "email2"));
        message.setTitle("Title");
        message.setPriority(new TaskExtraInfo("Priority", "#008000"));
        message.setStatus(new TaskExtraInfo("Status", "#008000"));
        message.setOrganizationName("Organization name");
        message.setOrganizationId(1L);
        message.setDueDate(Instant.now().plusSeconds(2400));
        message.setCreatedAt(Instant.now().minusSeconds(2000));

        Mockito.doReturn(Mockito.mock(MimeMessage.class)).when(javaMailSender)
                .createMimeMessage();

        consumer.remindAboutTask(message);

        Mockito.verify(javaMailSender, Mockito.atLeastOnce()).send(Mockito.any(MimeMessage.class));
    }

}
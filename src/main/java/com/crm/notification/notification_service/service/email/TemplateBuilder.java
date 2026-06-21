package com.crm.notification.notification_service.service.email;


import com.crm.sharedlib.messaging.dto.amqp.TaskReminderMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class TemplateBuilder {

    private final TemplateEngine templateEngine;

    public String buildPasswordEmail(String password) {

        Context context = new Context();
        context.setVariable("password", password);

        return templateEngine.process("passwordEmailTemplate", context);
    }

    public String buildVerificationCodeEmail(String verificationCode) {

        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);

        return templateEngine.process("verificationCodeEmailTemplate", context);
    }


    public String buildInvitationEmail(UUID invitationId, String organizationName, String frontendUrl) {
        Context context = new Context();

        context.setVariable("organizationName", organizationName);
        context.setVariable("invitationId", invitationId);
        context.setVariable("frontendUrl", frontendUrl);

        return templateEngine.process("invitationEmailTemplate.html", context);
    }

    public String buildTaskReminderEmail(TaskReminderMessage message, String frontendUrl) {
        Context context = new Context();
        context.setVariable("title", message.getTitle());
        context.setVariable("taskId", message.getTaskId());

        context.setVariable("statusName", message.getStatus().getName());
        context.setVariable("statusColor", message.getStatus().getColor());

        context.setVariable("frontendUrl", frontendUrl);

        context.setVariable("priorityName", message.getPriority().getName());
        context.setVariable("priorityColor", message.getPriority().getColor());

        // TODO: For each user should be used different Zone ID
        if (nonNull(message.getDueDate())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                    .withZone(ZoneId.of("Europe/Kyiv"));

            context.setVariable("dueDate", formatter.format(message.getDueDate()));
        }

        return templateEngine.process("taskReminderEmailTemplate", context);
    }

}

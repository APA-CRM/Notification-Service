package com.crm.notification.notification_service.service.email;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

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

}

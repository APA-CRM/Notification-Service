package com.crm.notification.notification_service.controller;

import com.crm.notification.notification_service.dto.request.TestMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/notifications")
@RequiredArgsConstructor
public class TestNotificationController {

    private final SimpMessagingTemplate template;

    @PostMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendMessageToUser(
            @PathVariable("userId") Long userId,
            @RequestBody TestMessageRequest request
    ) {
        template.convertAndSendToUser(userId.toString(), "/topic/notifications", request.getMessage());
    }

    @PostMapping("/organizations/{organizationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendMessageToOrganization(
            @PathVariable("organizationId") Long organizationId,
            @RequestBody TestMessageRequest request
    ) {
        template.convertAndSend("/topic/organizations/%d/notifications".formatted(organizationId), request.getMessage());
    }

}

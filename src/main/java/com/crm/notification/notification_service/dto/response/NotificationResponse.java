package com.crm.notification.notification_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationResponse {

    private String messageCode;

    private String message;

    private Map<String, Object> details;

}

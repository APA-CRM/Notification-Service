package com.crm.notification.notification_service.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.crm.notification.notification_service.feign")
public class FeignConfig {
}

package com.crm.notification.notification_service.feign;

import com.crm.sharedlib.core.dto.response.AuthResponse;
import com.crm.sharedlib.core.feign.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "auth-service", configuration = FeignClientConfig.class)
public interface AuthClient {

    @GetMapping("/api/auth/authorize")
    AuthResponse authorize(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);

}

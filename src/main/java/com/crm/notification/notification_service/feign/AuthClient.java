package com.crm.notification.notification_service.feign;

import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import com.crm.sharedlib.core.feign.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${app.clients.auth-service.name}", configuration = FeignClientConfig.class)
public interface AuthClient {

    @GetMapping("/api/internal/auth/authorize")
    AuthResponse authorize(@RequestBody AuthorizationRequest request);

}

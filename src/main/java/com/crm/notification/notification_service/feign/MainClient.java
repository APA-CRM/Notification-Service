package com.crm.notification.notification_service.feign;

import com.crm.sharedlib.core.dto.response.UserExistsInOrganizationResponse;
import com.crm.sharedlib.core.feign.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${app.clients.main-service.name}", configuration = FeignClientConfig.class)
public interface MainClient {

    @GetMapping("/api/internal/organizations/{organizationId}/users/{userId}/exists")
    UserExistsInOrganizationResponse isUserExistsInOrganization(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("userId") Long userId
    );

}

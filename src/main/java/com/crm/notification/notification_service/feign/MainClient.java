package com.crm.notification.notification_service.feign;

import com.crm.sharedlib.dto.response.UserExistsInOrganizationResponse;
import com.crm.sharedlib.feign.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "main-service", configuration = FeignClientConfig.class)
public interface MainClient {

    @GetMapping("/api/internal/organizations/{organizationId}/users/{userId}/exists")
    UserExistsInOrganizationResponse isUserExistsInOrganization(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("userId") Long userId
    );

}

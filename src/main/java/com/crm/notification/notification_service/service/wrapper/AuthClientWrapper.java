package com.crm.notification.notification_service.service.wrapper;

import com.crm.notification.notification_service.feign.AuthClient;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthClientWrapper {

    private final AuthClient authClient;

    public AuthResponse authorize(String accessToken) {
        AuthorizationRequest request = new AuthorizationRequest();
        request.setAccessToken(accessToken);


        return authClient.authorize(request);
    }

}

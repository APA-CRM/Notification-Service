package com.crm.notification.notification_service.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.AbstractHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class UserHandshakeHandler extends AbstractHandshakeHandler {

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String userId = request.getHeaders().getFirst("User-Id");

        return () -> userId;
    }
}

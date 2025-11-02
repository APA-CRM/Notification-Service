package com.crm.notification.notification_service.config.websocket;

import com.crm.notification.notification_service.feign.AuthClient;
import com.crm.sharedlib.dto.response.AuthResponse;
import com.crm.sharedlib.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final AuthClient authClient;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        switch (accessor.getCommand()) {
            case CONNECT -> handleConnectMessage(accessor);
            case SUBSCRIBE -> handleSubscribeMessage(accessor);
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }


    private void handleConnectMessage(StompHeaderAccessor accessor) {
        String authorizationHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

        AuthResponse response = authClient.authorize(authorizationHeader);

        accessor.setUser(() -> response.getId().toString());
        accessor.setLeaveMutable(true);
    }

    private void handleSubscribeMessage(StompHeaderAccessor accessor) {
        if (isNull(accessor.getUser())) {
            throw new UnauthorizedException("Unauthorized");
        }
    }
}

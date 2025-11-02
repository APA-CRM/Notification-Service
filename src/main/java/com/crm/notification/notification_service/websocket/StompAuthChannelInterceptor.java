package com.crm.notification.notification_service.websocket;

import com.crm.notification.notification_service.feign.AuthClient;
import com.crm.sharedlib.dto.response.AuthResponse;
import com.crm.sharedlib.exception.ForbiddenException;
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

        return switch (accessor.getCommand()) {
            case CONNECT -> handleConnectMessage(message, accessor);
            case SUBSCRIBE -> handleSubscribeMessage(message, accessor);
            case SEND, MESSAGE -> throw new ForbiddenException("You not allowed to send a message");
            default -> message;
        };

    }


    private Message<?> handleConnectMessage(Message<?> message, StompHeaderAccessor accessor) {
        String authorizationHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

        AuthResponse response = authClient.authorize(authorizationHeader);

        accessor.setUser(() -> response.getId().toString());
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

    private Message<?> handleSubscribeMessage(Message<?> message, StompHeaderAccessor accessor) {
        if (isNull(accessor.getUser())) {
            throw new UnauthorizedException("Unauthorized");
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }
}

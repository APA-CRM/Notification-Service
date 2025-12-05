package com.crm.notification.notification_service.websocket;

import com.crm.notification.notification_service.feign.AuthClient;
import com.crm.notification.notification_service.feign.MainClient;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import com.crm.sharedlib.core.dto.response.UserExistsInOrganizationResponse;
import com.crm.sharedlib.core.exception.ForbiddenException;
import com.crm.sharedlib.core.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final static Pattern ORGANIZATION_NOTIFICATION_TOPIC_PATTERN =
            Pattern.compile("^/topic/organizations/(?<organizationId>\\d+)/notifications$");

    private final AuthClient authClient;
    private final MainClient mainClient;

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

        try {
            AuthResponse response = authClient.authorize(authorizationHeader);

            accessor.setUser(() -> response.getId().toString());
            accessor.setLeaveMutable(true);
        } catch (UnauthorizedException e) {
            return null;
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

    private Message<?> handleSubscribeMessage(Message<?> message, StompHeaderAccessor accessor) {
        if (isNull(accessor.getUser())) {
            return null;
        }

        if (nonNull(accessor.getDestination())) {
            Matcher matcher = ORGANIZATION_NOTIFICATION_TOPIC_PATTERN.matcher(accessor.getDestination());

            if (matcher.matches()) {
                String organizationId = matcher.group("organizationId");
                return handleSubscribeToOrganizationNotifications(message, accessor, Long.valueOf(organizationId));
            }
        }

        return message;
    }

    private Message<?> handleSubscribeToOrganizationNotifications(Message<?> message, StompHeaderAccessor accessor, Long organizationId) {
        UserExistsInOrganizationResponse userExistsInOrganization =
                mainClient.isUserExistsInOrganization(organizationId, Long.valueOf(accessor.getUser().getName()));

        if (userExistsInOrganization.getIsUserExistsInOrganization()) {
            return message;
        }

        return null;
    }
}

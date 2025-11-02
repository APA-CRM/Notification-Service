package com.crm.notification.notification_service.websocket;

import com.crm.sharedlib.exception.UnauthorizedException;
import com.crm.sharedlib.exception.response.CrmErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
@RequiredArgsConstructor
public class CustomStompErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        if (ex instanceof UnauthorizedException) {
            accessor.setMessage("Unauthorized");
            accessor.setHeader("status", 401);
        } else {
            accessor.setMessage("Internal server error");
            accessor.setHeader("status", 500);
        }

        CrmErrorResponse response = new CrmErrorResponse(ex.getMessage());

        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(objectMapper.writeValueAsBytes(response), accessor.getMessageHeaders());
    }
}

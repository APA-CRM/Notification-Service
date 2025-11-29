package com.crm.notification.notification_service.service;

import com.crm.notification.notification_service.BaseIntegrationTest;
import com.crm.notification.notification_service.dto.response.NotificationResponse;
import com.crm.notification.notification_service.feign.AuthClient;
import com.crm.notification.notification_service.feign.MainClient;
import com.crm.sharedlib.dto.CrmMessage;
import com.crm.sharedlib.dto.CrmNotification;
import com.crm.sharedlib.dto.CrmRecipient;
import com.crm.sharedlib.dto.response.AuthResponse;
import com.crm.sharedlib.dto.response.UserExistsInOrganizationResponse;
import com.crm.sharedlib.enums.RecipientType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class NotificationSenderTest extends BaseIntegrationTest {

    private final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

    @MockitoBean
    private AuthClient authClient;
    @MockitoBean
    private MainClient mainClient;

    private WebSocketStompClient stompClient;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    }

    @Test
    @DisplayName("Connect to WebSockets and receive message")
    public void connectAndSubscribeToOrganizationNotificationExpectedSuccess() throws Exception {
        when(authClient.authorize(anyString()))
                .thenReturn(new AuthResponse(1, ""));

        when(mainClient.isUserExistsInOrganization(anyLong(), anyLong()))
                .thenReturn(new UserExistsInOrganizationResponse(true));

        String topicName = "/topic/organizations/1/notifications";

        String url = "ws://localhost:" + localServerPort + "/ws-notifications";

        CompletableFuture<Object> messageFuture = new CompletableFuture<>();
        CompletableFuture<Throwable> errorFuture = new CompletableFuture<>();

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe(topicName, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return byte[].class;
                    }

                    @Override
                    @SneakyThrows
                    public void handleFrame(StompHeaders headers, Object payload) {
                        messageFuture.complete(objectMapper.readValue((byte[]) payload, NotificationResponse.class));
                    }
                });
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                        byte[] payload, Throwable exception) {
                errorFuture.complete(exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                errorFuture.complete(exception);
            }
        };

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer good-token");

        CompletableFuture<StompSession> f = stompClient.connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, sessionHandler);
        StompSession session = null;
        try {
            session = f.get(CONNECT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            fail("Fail to connect to WebSockets", e);
        }

        Thread.sleep(500);

        CrmNotification notification = CrmNotification.builder()
                .message(new CrmMessage("HELLO_WORLD", "Hello World!", null))
                .recipient(new CrmRecipient(1L, RecipientType.ORGANIZATION))
                .build();

        notificationSender.sendNotification(notification);

        NotificationResponse receivedMessage = (NotificationResponse) messageFuture.get(
                1000,
                TimeUnit.MILLISECONDS
        );

        assertEquals(notification.getMessage().getCode(), receivedMessage.getMessageCode());
        assertEquals(notification.getMessage().getMessage(), receivedMessage.getMessage());

        session.disconnect();

        verify(mainClient, atLeastOnce()).isUserExistsInOrganization(anyLong(), anyLong());
    }

}
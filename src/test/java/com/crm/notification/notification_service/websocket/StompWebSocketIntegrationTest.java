package com.crm.notification.notification_service.websocket;

import com.crm.notification.notification_service.BaseIntegrationTest;
import com.crm.notification.notification_service.feign.AuthClient;
import com.crm.notification.notification_service.feign.MainClient;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import com.crm.sharedlib.core.dto.response.UserExistsInOrganizationResponse;
import com.crm.sharedlib.core.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class StompWebSocketIntegrationTest extends BaseIntegrationTest {

    private final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

    @MockitoBean
    private AuthClient authClient;
    @MockitoBean
    private MainClient mainClient;

    private WebSocketStompClient stompClient;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    }

    @Test
    @DisplayName("Connect with authorization token expected success")
    public void connectWithAuthorizationExpectedSuccess() throws Exception {
        when(authClient.authorize(anyString())).thenReturn(new AuthResponse(1, ""));

        String url = "ws://localhost:" + localServerPort + "/ws-notifications";

        CompletableFuture<String> messageFuture = new CompletableFuture<>();
        CompletableFuture<Throwable> errorFuture = new CompletableFuture<>();

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/test", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return byte[].class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        messageFuture.complete(new String((byte[]) payload));
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
        StompSession session = f.get(CONNECT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

        session.disconnect();
    }

    @Test
    @DisplayName("Connect with authorization token expected success")
    public void connectAndSubscribeToOrganizationNotificationExpectedSuccess() throws Exception {
        when(authClient.authorize(anyString()))
                .thenReturn(new AuthResponse(1, ""));

        when(mainClient.isUserExistsInOrganization(anyLong(), anyLong()))
                .thenReturn(new UserExistsInOrganizationResponse(true));

        String topicName = "/topic/organizations/1/notifications";

        String url = "ws://localhost:" + localServerPort + "/ws-notifications";

        CompletableFuture<String> messageFuture = new CompletableFuture<>();
        CompletableFuture<Throwable> errorFuture = new CompletableFuture<>();

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe(topicName, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        messageFuture.complete(new String((byte[]) payload));
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

        simpMessagingTemplate.convertAndSend(topicName, "Hello World!");

        assertTrue(session.isConnected());
        session.disconnect();

        verify(mainClient, atLeastOnce()).isUserExistsInOrganization(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Connect without authorization token expected error")
    public void connectWithoutAuthorizationExpectedError() throws Exception {

        when(authClient.authorize(null)).thenThrow(new UnauthorizedException("Unauthorized"));

        String url = "ws://localhost:" + localServerPort + "/ws-notifications";

        CompletableFuture<StompHeaders> errorFrameFuture = new CompletableFuture<>();
        CompletableFuture<Throwable> transportError = new CompletableFuture<>();

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/test", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return byte[].class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        errorFrameFuture.complete(headers);
                    }
                });
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorFrameFuture.complete(headers);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                transportError.complete(exception);
            }
        };


        CompletableFuture<StompSession> f = stompClient.connectAsync(url, sessionHandler);
        StompSession session = null;
        try {
            session = f.get(CONNECT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException ee) {
            //  Message has been ignored because of the error
            return;
        }

        try {
            StompHeaders headers = errorFrameFuture.get(2, TimeUnit.SECONDS);
            assertNotNull(headers);
        } catch (TimeoutException te) {
            if (transportError.isDone()) {
                Throwable ex = transportError.getNow(null);
                assertNotNull(ex);
            } else {
                fail("Expected subscribe to be rejected but no ERROR or transport error received.");
            }
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

}

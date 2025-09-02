package com.team1.soai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WebSocketConnectionService {

    private final Map<String, WebSocketSession> connections = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<String>> responseFutures = new ConcurrentHashMap<>();

    public void connect(String connectionKey, String wsUrl) {
        try {
            WebSocketSession existingSession = connections.get(connectionKey);
            if (existingSession != null && existingSession.isOpen()) {
                log.info("WebSocket connection already exists for key: {}", connectionKey);
                return;
            }

            WebSocketHandler handler = new WebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                    log.info("FastAPI WebSocket connection established for {}: {}", connectionKey, session.getId());
                    connections.put(connectionKey, session);
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                    String responseText = message.getPayload().toString();
                    log.info("Received message from FastAPI [{}]: {}", connectionKey, responseText);

                    CompletableFuture<String> future = responseFutures.get(connectionKey);
                    if (future != null && !future.isDone()) {
                        future.complete(responseText);
                    }
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                    log.error("FastAPI WebSocket transport error for {}", connectionKey, exception);
                    CompletableFuture<String> future = responseFutures.get(connectionKey);
                    if (future != null && !future.isDone()) {
                        future.completeExceptionally(exception);
                    }
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                    log.info("FastAPI WebSocket connection closed for {}: {}", connectionKey, closeStatus);
                    connections.remove(connectionKey);
                    responseFutures.remove(connectionKey);
                }

                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }
            };

            WebSocketClient client = new StandardWebSocketClient();
            client.doHandshake(handler, null, URI.create(wsUrl)).get(180, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("Failed to connect to FastAPI WebSocket for key: {}", connectionKey, e);
            throw new RuntimeException("FastAPI WebSocket connection failed for " + connectionKey, e);
        }
    }

    public String sendMessage(String connectionKey, String message, int timeoutSeconds) {
        try {
            WebSocketSession session = connections.get(connectionKey);
            if (session == null || !session.isOpen()) {
                throw new RuntimeException("FastAPI WebSocket is not connected for key: " + connectionKey);
            }

            // 응답을 기다리기 위한 Future 생성
            CompletableFuture<String> future = new CompletableFuture<>();
            responseFutures.put(connectionKey, future);

            // FastAPI WebSocket으로 메시지 전송
            session.sendMessage(new TextMessage(message));
            log.info("Sent message to FastAPI [{}]: {}", connectionKey, message);

            // 응답 대기
            String response = future.get(timeoutSeconds, TimeUnit.SECONDS);
            log.info("Received response from FastAPI [{}]: {}", connectionKey, response);

            return response;

        } catch (Exception e) {
            log.error("Failed to send message to FastAPI [{}] or receive response", connectionKey, e);
            return "Error: " + e.getMessage();
        } finally {
            responseFutures.remove(connectionKey);
        }
    }

    public void disconnect(String connectionKey) {
        try {
            WebSocketSession session = connections.get(connectionKey);
            if (session != null && session.isOpen()) {
                session.close();
                log.info("FastAPI WebSocket connection closed for key: {}", connectionKey);
            }
            connections.remove(connectionKey);
            responseFutures.remove(connectionKey);
        } catch (Exception e) {
            log.error("Error closing FastAPI WebSocket connection for key: {}", connectionKey, e);
        }
    }

    public boolean isConnected(String connectionKey) {
        WebSocketSession session = connections.get(connectionKey);
        return session != null && session.isOpen();
    }

    public void disconnectAll() {
        for (String key : connections.keySet()) {
            disconnect(key);
        }
    }
}
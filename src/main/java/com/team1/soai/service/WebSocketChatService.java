package com.team1.soai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketChatService {

    private final WebSocketConnectionService connectionService;

    @Value("${fastapi.websocket.url}")
    private String fastApiWebSocketUrl;

    private static final String CHAT_CONNECTION_KEY = "chat";

    public void connect(String wsUrl) {
        if (!connectionService.isConnected(CHAT_CONNECTION_KEY)) {
            connectionService.connect(CHAT_CONNECTION_KEY, wsUrl);
        } else {
            log.info("WebSocket already connected for key: {}", CHAT_CONNECTION_KEY);
        }
    }

    public String sendMessage(String message) {
        if (!connectionService.isConnected(CHAT_CONNECTION_KEY)) {
            throw new RuntimeException("WebSocket is not connected");
        }
        // connectionService가 메시지 전송과 응답 대기를 처리
        return connectionService.sendMessage(CHAT_CONNECTION_KEY, message, 180);
    }

    public void disconnect() {
        connectionService.disconnect(CHAT_CONNECTION_KEY);
    }

    public boolean isConnected() {
        return connectionService.isConnected(CHAT_CONNECTION_KEY);
    }
}

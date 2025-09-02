package com.team1.soai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketCaptioningService {

    @Value("${fastapi.websocket.url}")
    private String fastApiWebSocketUrl;

    private final WebSocketConnectionService connectionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CAPTIONING_CONNECTION_KEY = "captioning";
    private static final int CAPTIONING_TIMEOUT_SECONDS = 240; // 이미지 처리는 더 오래 걸릴 수 있음

    public void connect(String wsUrl) {
        connectionService.connect(CAPTIONING_CONNECTION_KEY, wsUrl);
    }

    public String sendCaptioningRequest(String instanceUuid, String description) {
        try {
            // 요청 데이터 준비 (FastAPI의 WebSocket 엔드포인트 형식에 맞춤)
            Map<String, String> request = new HashMap<>();
            request.put("instanceUUID", instanceUuid);
            request.put("description", description);

            String jsonRequest = objectMapper.writeValueAsString(request);

            return connectionService.sendMessage(CAPTIONING_CONNECTION_KEY, jsonRequest, CAPTIONING_TIMEOUT_SECONDS);

        } catch (Exception e) {
            log.error("Failed to send captioning request", e);
            return "Error: 캡셔닝 요청 처리 중 오류가 발생했습니다.";
        }
    }

    public void disconnect() {
        connectionService.disconnect(CAPTIONING_CONNECTION_KEY);
    }

    public boolean isConnected() {
        return connectionService.isConnected(CAPTIONING_CONNECTION_KEY);
    }
}
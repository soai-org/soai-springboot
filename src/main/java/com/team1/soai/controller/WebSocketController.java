package com.team1.soai.controller;

import com.team1.soai.service.ChatService;
import com.team1.soai.service.WebSocketCaptioningService;
import com.team1.soai.service.WebSocketChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    @Value("${fastapi.websocket.url:ws://localhost:8000}")
    private String fastApiWebSocketUrl;

    private final ChatService chatService;
    private final WebSocketChatService webSocketChatService;
    private final WebSocketCaptioningService webSocketCaptioningService;

    @MessageMapping("/ws-chat")
    @SendTo("/sub/chat")
    public Map<String, String> handleChat(String message) {
        try {
            // FastAPI WebSocket URL 구성
            String wsUrl = fastApiWebSocketUrl + "/chat-bot/ws-llm";

            // WebSocket 연결
            webSocketChatService.connect(wsUrl);

            // 메시지 전송 및 응답 수신
            String response = webSocketChatService.sendMessage(message);

            // 채팅 히스토리 저장
            chatService.saveChatHistory(message, response);

            log.info("Chat processed successfully - Message: {}, Response: {}", message, response);

            return Map.of(
                    "message", message,
                    "response", response
            );

        } catch (Exception e) {
            log.error("Error processing chat message", e);
            return Map.of(
                    "message", message,
                    "response", "죄송합니다. 메시지 처리 중 오류가 발생했습니다."
            );
        }
    }

    @MessageMapping("/ws-captioning")
    @SendTo("/sub/ws-captioning")
    public Map<String, Object> handleCaptioning(Map<String, String> request) {
        try {
            String instanceUuid = request.get("instanceUUID");
            String description = request.get("description");

            // Validation
            if (instanceUuid == null || instanceUuid.trim().isEmpty()) {
                return Map.of(
                        "status", "error",
                        "message", "instanceUUID is required"
                );
            }

            if (description == null || description.trim().isEmpty()) {
                return Map.of(
                        "status", "error",
                        "message", "description is required"
                );
            }

            // FastAPI WebSocket URL 구성
            String wsUrl = fastApiWebSocketUrl + "/chat-bot/ws-diagnosis";

            // WebSocket 연결
            webSocketCaptioningService.connect(wsUrl);

            // 캡셔닝 요청 전송 및 응답 수신
            String transcript = webSocketCaptioningService.sendCaptioningRequest(instanceUuid, description);

            log.info("Captioning processed successfully - Instance: {}, Description: {}", instanceUuid, description);

            return Map.of(
                    "status", "success",
                    "instanceUUID", instanceUuid,
                    "transcript", transcript
            );

        } catch (Exception e) {
            log.error("Error processing captioning request", e);
            return Map.of(
                    "status", "error",
                    "message", "캡셔닝 처리 중 오류가 발생했습니다.",
                    "error", e.getMessage()
            );
        }
    }
}
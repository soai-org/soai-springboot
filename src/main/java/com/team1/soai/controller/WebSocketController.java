package com.team1.soai.controller;

import com.team1.soai.service.WsRelayHandler;
import com.team1.soai.service.WsRelayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/websocket")
@RequiredArgsConstructor
public class WebSocketController {

    private final WsRelayService relayService;
    private final WsRelayHandler relayHandler;

    // 클라이언트 세션 관리 - WsRelayHandler와 공유
    private Map<String, WebSocketSession> getClientSessions() {
        return relayHandler.getClientSessions();
    }

    /**
     * WebSocket 연결 상태 확인
     */
    @GetMapping("/status")
    public Map<String, Object> getWebSocketStatus() {
        Map<String, Object> status = new ConcurrentHashMap<>();
        status.put("activeConnections", getClientSessions().size());
        status.put("timestamp", System.currentTimeMillis());
        status.put("status", "active");
        return status;
    }

    /**
     * 특정 클라이언트의 WebSocket 연결 상태 확인
     */
    @GetMapping("/status/{clientId}")
    public Map<String, Object> getClientStatus(@PathVariable String clientId) {
        Map<String, Object> status = new ConcurrentHashMap<>();
        WebSocketSession session = getClientSessions().get(clientId);
        
        if (session != null) {
            status.put("clientId", clientId);
            status.put("connected", session.isOpen());
            status.put("remoteAddress", session.getRemoteAddress());
            status.put("lastActivity", System.currentTimeMillis());
        } else {
            status.put("clientId", clientId);
            status.put("connected", false);
            status.put("message", "Client not found");
        }
        
        return status;
    }

    /**
     * WebSocket 연결을 통한 메시지 전송
     */
    @PostMapping("/send/{clientId}")
    public Map<String, Object> sendMessageToClient(
            @PathVariable String clientId,
            @RequestBody Map<String, String> request) {
        
        Map<String, Object> response = new ConcurrentHashMap<>();
        WebSocketSession session = getClientSessions().get(clientId);
        
        try {
            if (session != null && session.isOpen()) {
                String message = request.get("message");
                String messageType = request.getOrDefault("type", "TEXT");
                
                if ("LLM".equals(messageType)) {
                    session.sendMessage(new TextMessage("LLM:" + message));
                } else if ("DIAGNOSIS".equals(messageType)) {
                    session.sendMessage(new TextMessage("DIAGNOSIS:" + message));
                } else if ("NLP".equals(messageType)) {
                    session.sendMessage(new TextMessage("NLP:" + message));
                } else {
                    session.sendMessage(new TextMessage(message));
                }
                
                response.put("success", true);
                response.put("message", "Message sent successfully");
                response.put("clientId", clientId);
            } else {
                response.put("success", false);
                response.put("message", "Client not connected");
                response.put("clientId", clientId);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send message: " + e.getMessage());
            response.put("clientId", clientId);
            log.error("Failed to send message to client {}", clientId, e);
        }
        
        return response;
    }

    /**
     * 모든 활성 WebSocket 연결 목록 조회
     */
    @GetMapping("/connections")
    public Map<String, Object> getAllConnections() {
        Map<String, Object> connections = new ConcurrentHashMap<>();
        connections.put("totalConnections", getClientSessions().size());
        connections.put("activeConnections", getClientSessions().entrySet().stream()
                .filter(entry -> entry.getValue().isOpen())
                .map(Map.Entry::getKey)
                .toList());
        connections.put("timestamp", System.currentTimeMillis());
        return connections;
    }

    /**
     * 특정 클라이언트 연결 강제 종료
     */
    @DeleteMapping("/disconnect/{clientId}")
    public Map<String, Object> disconnectClient(@PathVariable String clientId) {
        Map<String, Object> response = new ConcurrentHashMap<>();
        
        try {
            WebSocketSession session = getClientSessions().get(clientId);
            if (session != null) {
                if (session.isOpen()) {
                    session.close();
                }
                getClientSessions().remove(clientId);
                response.put("success", true);
                response.put("message", "Client disconnected successfully");
                response.put("clientId", clientId);

            } else {
                response.put("success", false);
                response.put("message", "Client not found");
                response.put("clientId", clientId);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to disconnect client: " + e.getMessage());
            response.put("clientId", clientId);
            log.error("Failed to disconnect client {}", clientId, e);
        }
        
        return response;
    }


}

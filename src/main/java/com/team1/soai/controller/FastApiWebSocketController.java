package com.team1.soai.controller;

import com.team1.soai.service.FastApiWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/fastapi/websocket")
@RequiredArgsConstructor
public class FastApiWebSocketController {

    private final FastApiWebSocketService fastApiWebSocketService;

    /**
     * LLM 스트리밍 시작 - 전체 과정 자동화
     * 1. 세션 생성 (기존 클라이언트 세션 찾기)
     * 2. FastAPI WebSocket 연결 생성
     * 3. LLM 스트리밍 시작 및 데이터 릴레이
     */
    @PostMapping("/llm/start")
    public ResponseEntity<Map<String, Object>> startLlmStreaming(
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String clientId = request.get("clientId");
            String prompt = request.get("prompt");
            
            if (clientId == null || prompt == null) {
                response.put("success", false);
                response.put("message", "clientId and prompt are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            log.info("LLM streaming 시작 요청: clientId={}, prompt={}", clientId, prompt);
            
            try {
                // 1. 세션 생성 (기존 클라이언트 세션을 찾거나 새로 생성)
                String sessionKey = "llm-" + clientId;
                fastApiWebSocketService.createSession(sessionKey, clientId);
                
                // 2. 클라이언트 WebSocket 자동 생성 및 연결 (Spring Boot ↔ 클라이언트)
                // 이 부분은 HTML 클라이언트가 먼저 Spring Boot WebSocket에 연결하도록 변경되었으므로,
                // 여기서 새로운 클라이언트 WebSocket을 생성하는 로직은 제거됨.
                // WebSocketSession clientSession = fastApiWebSocketService.createClientWebSocket(sessionKey, clientId); // REMOVED
                
                // 3. FastAPI WebSocket 연결 생성 및 LLM 스트리밍 시작 (Spring Boot ↔ FastAPI)
                // 이 메서드가 FastAPI의 @router.websocket("/ws-llm-stream")을 호출하고
                // 스트리밍 데이터를 받아서 클라이언트로 전달하며, 완료 시 자동으로 연결 종료
                fastApiWebSocketService.startLlmStreamingWithRelay(sessionKey, prompt);
                
                response.put("success", true);
                response.put("message", "LLM streaming started successfully - ALL WebSocket connections created automatically!");
                response.put("clientId", clientId);
                response.put("sessionKey", sessionKey);
                // response.put("clientWebSocketId", clientSession.getId()); // REMOVED
                response.put("status", "streaming");
                response.put("autoConnected", true);
                
                log.info("LLM streaming started for client: {} with prompt: {} - ALL WebSocket connections created automatically!", clientId, prompt);
                
                return ResponseEntity.ok(response);
                
            } catch (Exception e) {
                log.error("Failed to start LLM streaming", e);
                response.put("success", false);
                response.put("message", "Failed to start LLM streaming: " + e.getMessage());
                return ResponseEntity.status(500).body(response);
            }
            
        } catch (Exception e) {
            log.error("Unexpected error in startLlmStreaming", e);
            response.put("success", false);
            response.put("message", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Diagnosis 스트리밍 시작 - 전체 과정 자동화
     * 1. 세션 생성 (기존 클라이언트 세션 찾기)
     * 2. FastAPI WebSocket 연결 생성
     * 3. Diagnosis 스트리밍 시작 및 데이터 릴레이
     */
    @PostMapping("/diagnosis/start")
    public ResponseEntity<Map<String, Object>> startDiagnosisStreaming(
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            // JWT 토큰 검증 (테스트용으로 비활성화)
            // String token = authHeader.replace("Bearer ", "");
            // if (!jwtService.validateToken(token)) {
            //     response.put("success", false);
            //     response.put("message", "Invalid JWT token");
            //     return ResponseEntity.status(401).body(response);
            // }

            String clientId = request.get("clientId");
            String instanceUuid = request.get("instanceUuid");
            String description = request.get("description");

            if (clientId == null || instanceUuid == null ||  description == null) {
                response.put("success", false);
                response.put("message", "clientId, instanceUuid and description are required");
                return ResponseEntity.badRequest().body(response);
            }

            try {
                // 1. 세션 생성 (기존 클라이언트 세션을 찾거나 새로 생성)
                String sessionKey = "llm-" + clientId;
                fastApiWebSocketService.createSession(sessionKey, clientId);

                // 2. 클라이언트 WebSocket 자동 생성 및 연결 (Spring Boot ↔ 클라이언트)
                // 이 부분은 HTML 클라이언트가 먼저 Spring Boot WebSocket에 연결하도록 변경되었으므로,
                // 여기서 새로운 클라이언트 WebSocket을 생성하는 로직은 제거됨.
                // WebSocketSession clientSession = fastApiWebSocketService.createClientWebSocket(sessionKey, clientId); // REMOVED

                // 3. FastAPI WebSocket 연결 생성 및 Diagnosis 스트리밍 시작 (Spring Boot ↔ FastAPI)
                // 이 메서드가 FastAPI의 @router.websocket("/ws-llm-stream")을 호출하고
                // 스트리밍 데이터를 받아서 클라이언트로 전달하며, 완료 시 자동으로 연결 종료
                fastApiWebSocketService.startDiagnosisStreamingWithRelay(sessionKey, instanceUuid, description);

                response.put("success", true);
                response.put("message", "Diagnosis streaming started successfully - ALL WebSocket connections created automatically!");
                response.put("clientId", clientId);
                response.put("sessionKey", sessionKey);
                // response.put("clientWebSocketId", clientSession.getId()); // REMOVED
                response.put("status", "streaming");
                response.put("autoConnected", true);

                log.info("LLM streaming started for client: {} - ALL WebSocket connections created automatically!", clientId);

                return ResponseEntity.ok(response);

            } catch (Exception e) {
                log.error("Failed to start Diagnosis streaming", e);
                response.put("success", false);
                response.put("message", "Failed to start Diagnosis streaming: " + e.getMessage());
                return ResponseEntity.status(500).body(response);
            }

        } catch (Exception e) {
            log.error("Unexpected error in startDiagnosisStreaming", e);
            response.put("success", false);
            response.put("message", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 현재 활성 WebSocket 연결 상태 확인
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getWebSocketStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("activeFastApiSessions", fastApiWebSocketService.getActiveSessions().size());
        status.put("activeClientSessions", fastApiWebSocketService.getClientSessions().size());
        status.put("timestamp", System.currentTimeMillis());
        status.put("status", "active");
        return ResponseEntity.ok(status);
    }

    /**
     * 특정 세션의 상태 확인
     */
    @GetMapping("/status/{sessionKey}")
    public ResponseEntity<Map<String, Object>> getSessionStatus(@PathVariable String sessionKey) {
        Map<String, Object> status = new HashMap<>();
        boolean isActive = fastApiWebSocketService.isSessionActive(sessionKey);
        
        status.put("sessionKey", sessionKey);
        status.put("active", isActive);
        status.put("timestamp", System.currentTimeMillis());
        
        if (isActive) {
            status.put("message", "Session is active and streaming");
        } else {
            status.put("message", "Session is not active");
        }
        
        return ResponseEntity.ok(status);
    }
}

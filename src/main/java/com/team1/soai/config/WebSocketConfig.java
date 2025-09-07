package com.team1.soai.config;

import com.team1.soai.service.FastApiWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final FastApiWebSocketService fastApiWebSocketService;
    
    // Spring Boot WebSocket 세션 관리
    private final Map<String, WebSocketSession> springSessions = new ConcurrentHashMap<>();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 1️⃣ 테스트용 WebSocket 핸들러 (/ws)
        registry.addHandler(new SimpleTestWebSocketHandler(), "/ws")
                .setAllowedOrigins("*");
        
        // 2️⃣ LLM 스트리밍용 WebSocket 핸들러 (/ws-llm)
        registry.addHandler(new LlmStreamingWebSocketHandler(), "/ws-llm")
                .setAllowedOrigins("*");
        
        log.info("✅ WebSocket 핸들러 등록 완료: /ws, /ws-llm");
    }

    // ===========================
    // 1️⃣ 테스트용 WebSocket 핸들러
    // ===========================
    private class SimpleTestWebSocketHandler extends TextWebSocketHandler {
        
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            log.info("✅ 테스트 WebSocket 연결 성공: {} from {}", session.getId(), session.getRemoteAddress());
            springSessions.put(session.getId(), session);
            
            // 클라이언트 세션을 FastApiWebSocketService에 등록
            // 1. 세션 ID 기반 키
            String sessionKey = "llm-" + session.getId();
            fastApiWebSocketService.registerClientSession(sessionKey, session);
            log.info("클라이언트 세션 등록 완료 (세션 ID 기반): {}", sessionKey);
            
            // 2. 고유 클라이언트 ID 기반 키 (컨트롤러에서 찾을 수 있도록)
            String clientId = "test-client-" + System.currentTimeMillis();
            String clientSessionKey = "llm-" + clientId;
            fastApiWebSocketService.registerClientSession(clientSessionKey, session);
            log.info("클라이언트 ID 기반 세션 등록 완료: {} -> {}", clientSessionKey, session.getId());
            
            // 3. 세션 객체에 clientId 저장 (나중에 참조용)
            session.getAttributes().put("clientId", clientId);
            
            // 디버깅을 위해 현재 등록된 세션들 출력
            log.info("현재 FastApiWebSocketService에 등록된 클라이언트 세션들: {}", 
                    fastApiWebSocketService.getClientSessions().keySet());
            
            // 연결 성공 메시지 전송
            session.sendMessage(new TextMessage("🚀 WebSocket 연결 성공!"));
            session.sendMessage(new TextMessage("🔑 클라이언트 ID: " + clientId));
        }
        
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String messageText = message.getPayload();
            log.info("📨 메시지 수신: {}", messageText);
            
            // LLM 메시지만 처리하고, 일반 메시지는 에코하지 않음
            if (messageText.startsWith("LLM:")) {
                String prompt = messageText.substring(4); // "LLM:" 제거
                log.info("🤖 LLM 프롬프트: {}", prompt);
                
                // Spring Boot 컨트롤러에서 이미 처리하므로 여기서는 별도 처리 없음
                try {
                    session.sendMessage(new TextMessage("✅ LLM 요청이 Spring Boot 컨트롤러로 전달되었습니다."));
                    session.sendMessage(new TextMessage("🔄 FastAPI 연결 및 스트리밍은 Spring Boot에서 자동으로 처리됩니다."));
                } catch (IOException e) {
                    log.error("메시지 전송 실패: {}", e.getMessage());
                }
                
                log.info("🤖 LLM 프롬프트 '{}' 처리 완료 - Spring Boot 컨트롤러에서 자동 처리됨", prompt);
            }
            // 일반 메시지는 에코하지 않음 - 중복 처리 방지
        }
        
        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            log.info("❌ 테스트 WebSocket 연결 종료: {} - {}", session.getId(), status);
            springSessions.remove(session.getId());
            
            // FastApiWebSocketService에서도 세션 제거
            String sessionKey = "llm-" + session.getId();
            fastApiWebSocketService.getClientSessions().remove(sessionKey);
            
            // clientId 기반 세션도 제거
            String clientId = (String) session.getAttributes().get("clientId");
            if (clientId != null) {
                String clientSessionKey = "llm-" + clientId;
                fastApiWebSocketService.getClientSessions().remove(clientSessionKey);
            }
        }
        
        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            log.error("❌ WebSocket 전송 오류: {} - {}", session.getId(), exception.getMessage());
        }
    }

    // ===========================
    // 2️⃣ LLM 스트리밍용 WebSocket 핸들러
    // ===========================
    private class LlmStreamingWebSocketHandler extends TextWebSocketHandler {
        
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            log.info("✅ LLM 스트리밍 WebSocket 연결 성공: {} from {}", session.getId(), session.getRemoteAddress());
            
            // 연결 성공 메시지 전송
            session.sendMessage(new TextMessage("🚀 LLM 스트리밍 WebSocket 연결 성공!"));
            session.sendMessage(new TextMessage("💡 이 엔드포인트는 향후 확장을 위해 준비되었습니다."));
        }
        
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String messageText = message.getPayload();
            log.info("📨 LLM 스트리밍 메시지 수신: {}", messageText);
            
            // 향후 확장을 위한 기본 메시지 처리
            session.sendMessage(new TextMessage("📝 메시지 수신됨: " + messageText));
        }
        
        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            log.info("❌ LLM 스트리밍 WebSocket 연결 종료: {} - {}", session.getId(), status);
        }
        
        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            log.error("❌ LLM 스트리밍 WebSocket 전송 오류: {} - {}", session.getId(), exception.getMessage());
        }
    }
}


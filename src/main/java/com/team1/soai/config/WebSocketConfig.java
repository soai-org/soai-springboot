package com.team1.soai.config;

import com.team1.soai.service.WsRelayHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${fastapi.websocket.url}")
    private String fastApiWsBaseUrl;

    private final WsRelayHandler wsRelayHandler;

    private final ConcurrentHashMap<String, WebSocketSession> springSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> fastApiSessions = new ConcurrentHashMap<>();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        try {
            // FastAPIProxyWebSocketHandler 등록
            FastAPIProxyWebSocketHandler handler = new FastAPIProxyWebSocketHandler();
            registry.addHandler(handler, "/ws-llm-stream")
                    .setAllowedOrigins("*");
            
            // WsRelayHandler 등록
            registry.addHandler(wsRelayHandler, "/ws-relay")
                    .setAllowedOrigins("*");
            
            log.info("WebSocket 핸들러 등록 완료: /ws-llm-stream, /ws-relay");
        } catch (Exception e) {
            log.error("WebSocket 핸들러 등록 실패: {}", e.getMessage(), e);
        }
    }


    public class FastAPIProxyWebSocketHandler extends TextWebSocketHandler {
        
        @Override
        public void afterConnectionEstablished(WebSocketSession springSession) throws Exception {
            springSessions.put(springSession.getId(), springSession);

            String urlTemplate = fastApiWsBaseUrl + "/ws-llm-stream";
            
            // FastAPI WebSocket 서버에 연결
            
            StandardWebSocketClient client = new StandardWebSocketClient();
            client.doHandshake(new TextWebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession fastApiSession) throws Exception {
                    fastApiSessions.put(springSession.getId(), fastApiSession);
                    
                    try {
                        springSession.sendMessage(new TextMessage("🚀 FastAPI 연결 완료! 이제 LLM 스트리밍을 받을 수 있습니다."));
                    } catch (Exception e) {
                        log.error("FastAPI 연결 완료 메시지 전송 실패: {}", e.getMessage(), e);
                    }
                }
                
                @Override
                protected void handleTextMessage(WebSocketSession fastApiSession, TextMessage message) throws Exception {
                    // FastAPI에서 받은 LLM 스트리밍 토큰을 Spring Boot 클라이언트에게 전달
                    if (springSession.isOpen()) {
                        try {
                            springSession.sendMessage(message);
                        } catch (Exception e) {
                            log.error("메시지 전달 실패: {}", e.getMessage(), e);
                        }
                    } else {
                        log.warn("Spring Boot 세션이 닫혀있음");
                    }
                }
                
                @Override
                public void afterConnectionClosed(WebSocketSession fastApiSession, org.springframework.web.socket.CloseStatus status) throws Exception {
                    log.info("FastAPI WebSocket 연결 종료: {}", status);
                    fastApiSessions.remove(springSession.getId());
                    if (springSession.isOpen()) {
                        try {
                            springSession.sendMessage(new TextMessage("FastAPI 연결 종료"));
                        } catch (Exception e) {
                            log.error("FastAPI 연결 종료 메시지 전송 실패: {}", e.getMessage());
                        }
                    }
                }
                
                @Override
                public void handleTransportError(WebSocketSession fastApiSession, Throwable exception) throws Exception {
                    log.error("FastAPI WebSocket 전송 오류: {}", exception.getMessage(), exception);
                }
            }, urlTemplate);
            

        }
        
        @Override
        protected void handleTextMessage(WebSocketSession springSession, TextMessage message) throws Exception {
            // Spring Boot 클라이언트에서 받은 메시지를 FastAPI로 전달
            WebSocketSession fastApiSession = fastApiSessions.get(springSession.getId());
            if (fastApiSession != null && fastApiSession.isOpen()) {
                try {
                    fastApiSession.sendMessage(message);
                } catch (Exception e) {
                    log.error("FastAPI로 메시지 전송 실패: {}", e.getMessage(), e);
                }
            } else {
                log.warn("FastAPI 세션이 없거나 닫혀있음");
                try {
                    springSession.sendMessage(new TextMessage("FastAPI 연결이 없습니다. 다시 연결해주세요."));
                } catch (Exception e) {
                    log.error("오류 메시지 전송 실패: {}", e.getMessage());
                }
            }
        }
        
        @Override
        public void afterConnectionClosed(WebSocketSession springSession, org.springframework.web.socket.CloseStatus status) throws Exception {
            springSessions.remove(springSession.getId());
            WebSocketSession fastApiSession = fastApiSessions.remove(springSession.getId());
            if (fastApiSession != null && fastApiSession.isOpen()) {
                fastApiSession.close();
            }
        }
        
        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            log.error("WebSocket 전송 오류: {}", exception.getMessage(), exception);
        }
    }

    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }
}


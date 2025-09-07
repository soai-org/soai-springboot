package com.team1.soai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WsRelayService {

    @Value("${fastapi.websocket.url}")
    private String fastApiWsBaseUrl;

    private final Map<String, WebSocketSession> fastapiSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();

    public synchronized WebSocketSession connectFastApi(String key, String path, WebSocketSession clientSession) throws Exception {
        WebSocketSession existing = fastapiSessions.get(key);
        if (existing != null && existing.isOpen()) {
            return existing;
        }

        WebSocketClient client = new StandardWebSocketClient();
        String url = fastApiWsBaseUrl + path;
        log.info("Connecting to FastAPI WS: {}", url);

        // 클라이언트 세션을 저장
        clientSessions.put(key, clientSession);

        WebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                log.info("Connected FastAPI WS session: {}", session.getId());
                fastapiSessions.put(key, session);
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                try {
                    String chunk = message.getPayload();
                    log.debug("FastAPI->Spring chunk: {}", chunk);
                    
                    // 클라이언트로 즉시 릴레이
                    WebSocketSession client = clientSessions.get(key);
                    if (client != null && client.isOpen()) {
                        client.sendMessage(new TextMessage(chunk));
                    }
                } catch (Exception e) {
                    log.error("Failed to relay chunk to client {}", key, e);
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                log.error("FastAPI WS transport error", exception);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                log.info("FastAPI WS closed: {} {}", session.getId(), status);
                fastapiSessions.remove(key);
                clientSessions.remove(key);
            }
        };

        return client.doHandshake(handler, url).get();
    }

    public void relayStreamingResponse(String clientId, String prompt, WebSocketSession clientSession) throws Exception {
        String fastApiKey = "llm-" + clientId;
        String fastApiPath = "/chat-bot/ws-llm";  // LLM 채팅용
        
        // FastAPI에 연결 (클라이언트 세션과 함께)
        WebSocketSession fastApiSession = connectFastApi(fastApiKey, fastApiPath, clientSession);
        
        // 프롬프트 전송
        fastApiSession.sendMessage(new TextMessage(prompt));
        
        log.info("Started LLM streaming relay for client {} with prompt: {}", clientId, prompt);
    }

    public void relayDiagnosisResponse(String clientId, String diagnosisData, WebSocketSession clientSession) throws Exception {
        String fastApiKey = "diagnosis-" + clientId;
        String fastApiPath = "/image-meta/ws-diagnosis";  // 이미지 진단용
        
        // FastAPI에 연결 (클라이언트 세션과 함께)
        WebSocketSession fastApiSession = connectFastApi(fastApiKey, fastApiPath, clientSession);
        
        // 진단 데이터 전송
        fastApiSession.sendMessage(new TextMessage(diagnosisData));
        
        log.info("Started diagnosis streaming relay for client {} with data: {}", clientId, diagnosisData);
    }

    public void relayNLPResponse(String clientId, String nlpData, WebSocketSession clientSession) throws Exception {
        String fastApiKey = "nlp-" + clientId;
        String fastApiPath = "/nlp/ws-nlp-stream";  // NLP 캡션 생성용
        
        // FastAPI에 연결 (클라이언트 세션과 함께)
        WebSocketSession fastApiSession = connectFastApi(fastApiKey, fastApiPath, clientSession);
        
        // NLP 데이터 전송 (텐서 + 메타 프롬프트)
        fastApiSession.sendMessage(new TextMessage(nlpData));
        
        log.info("Started NLP streaming relay for client {} with data: {}", clientId, nlpData);
    }
}



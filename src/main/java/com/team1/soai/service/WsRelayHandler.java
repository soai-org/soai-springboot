package com.team1.soai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WsRelayHandler extends TextWebSocketHandler {

    private final WsRelayService relayService;
    private final ConcurrentHashMap<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();
    
    // WebSocketController와 세션 공유를 위한 getter
    public ConcurrentHashMap<String, WebSocketSession> getClientSessions() {
        return clientSessions;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Client connected: {} from {}", session.getId(), session.getRemoteAddress());
        clientSessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession clientSession, TextMessage message) {
        String clientId = clientSession.getId();
        String messageText = message.getPayload();
        log.info("Received message from client {}: {}", clientId, messageText);

        try {
            // JSON 형태로 요청 타입과 데이터를 구분
            if (messageText.startsWith("LLM:")) {
                // LLM 채팅 요청
                String prompt = messageText.substring(4); // "LLM:" 제거
                relayService.relayStreamingResponse(clientId, prompt, clientSession);
            } else if (messageText.startsWith("DIAGNOSIS:")) {
                // 이미지 진단 요청
                String diagnosisData = messageText.substring(11); // "DIAGNOSIS:" 제거
                relayService.relayDiagnosisResponse(clientId, diagnosisData, clientSession);
            } else if (messageText.startsWith("NLP:")) {
                // NLP 캡션 생성 요청
                String nlpData = messageText.substring(4); // "NLP:" 제거
                relayService.relayNLPResponse(clientId, nlpData, clientSession);
            } else {
                // 기본값: LLM 채팅으로 처리
                relayService.relayStreamingResponse(clientId, messageText, clientSession);
            }
        } catch (Exception e) {
            log.error("Error relaying message for client {}", clientId, e);
            try {
                clientSession.sendMessage(new TextMessage("__error__: " + e.getMessage()));
            } catch (Exception sendError) {
                log.error("Failed to send error message", sendError);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Client disconnected: {} {} - Reason: {}", session.getId(), status, status.getReason());
        clientSessions.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Transport error for client {}: {}", session.getId(), exception.getMessage(), exception);
        clientSessions.remove(session.getId());
    }
}

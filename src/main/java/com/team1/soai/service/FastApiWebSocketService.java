package com.team1.soai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiWebSocketService {

    @Value("${fastapi.websocket.url}")
    private String fastApiWsBaseUrl;

    private final Map<String, WebSocketSession> fastapiSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();

    // 클라이언트 세션 등록
    public void registerClientSession(String sessionKey, WebSocketSession clientSession) {
        log.info("클라이언트 세션 등록: {} -> {}", sessionKey, clientSession.getId());
        clientSessions.put(sessionKey, clientSession);
    }

    // 클라이언트 세션 생성
    public void createSession(String sessionKey, String clientId) {
        log.info("Creating session: {} for client: {}", sessionKey, clientId);
        
        // 클라이언트 ID를 기반으로 이미 존재하는 클라이언트 WebSocket 세션을 찾아서 등록
        // WebSocketConfig에서 등록된 클라이언트 세션들을 확인
        for (Map.Entry<String, WebSocketSession> entry : clientSessions.entrySet()) {
            String existingSessionKey = entry.getKey();
            WebSocketSession existingSession = entry.getValue();
            
            // clientId가 포함된 세션 키를 찾거나, 세션이 활성 상태인지 확인
            if (existingSessionKey.contains(clientId) || 
                (existingSession != null && existingSession.isOpen())) {
                
                // 해당 세션을 새로운 sessionKey로 등록
                clientSessions.put(sessionKey, existingSession);
                log.info("클라이언트 세션 등록 완료: {} -> {} (기존 세션: {})", 
                        sessionKey, existingSession.getId(), existingSessionKey);
                return;
            }
        }
        
        // 기존 세션을 찾지 못한 경우, clientId를 기반으로 더 정확한 검색
        // HTML에서 전송하는 clientId와 WebSocket 세션 키의 패턴을 매칭
        String searchPattern = "llm-" + clientId;
        for (Map.Entry<String, WebSocketSession> entry : clientSessions.entrySet()) {
            String existingSessionKey = entry.getKey();
            WebSocketSession existingSession = entry.getValue();
            
            // 정확한 패턴 매칭 또는 부분 매칭
            if (existingSessionKey.equals(searchPattern) || 
                existingSessionKey.contains(clientId) ||
                (existingSession != null && existingSession.isOpen())) {
                
                clientSessions.put(sessionKey, existingSession);
                log.info("클라이언트 세션 등록 완료 (패턴 매칭): {} -> {} (기존 세션: {})", 
                        sessionKey, existingSession.getId(), existingSessionKey);
                return;
            }
        }
        
        // 여전히 찾지 못한 경우 로그 출력
        log.warn("클라이언트 세션을 찾을 수 없음: clientId={}, sessionKey={}", clientId, sessionKey);
        log.info("현재 등록된 클라이언트 세션들: {}", clientSessions.keySet());
        log.info("검색 패턴: {}", searchPattern);
    }

    // LLM 스트리밍 시작 + 데이터 릴레이
    public void startLlmStreamingWithRelay(String sessionKey, String prompt) throws Exception {
        log.info("Starting LLM streaming with relay for session: {} with prompt: {}", sessionKey, prompt);
        
        // FastAPI WebSocket 연결 생성
        StandardWebSocketClient client = new StandardWebSocketClient();
        String url = fastApiWsBaseUrl + "/chat-bot-ws/ws-llm-stream";
        
        // WebSocket 클라이언트 설정 (타임아웃 및 버퍼 크기 조정)
        Map<String, Object> properties = new HashMap<>();
        properties.put("org.apache.tomcat.websocket.IO_TIMEOUT_MS", "60000"); // 60초
        properties.put("org.apache.tomcat.websocket.WS_RCV_BUFFER_SIZE", "16384"); // 버퍼 크기
        properties.put("org.apache.tomcat.websocket.WS_SND_BUFFER_SIZE", "16384"); // 버퍼 크기
        properties.put("org.apache.tomcat.websocket.WS_CONNECT_TIMEOUT_MS", "30000"); // 연결 타임아웃
        client.setUserProperties(properties);
        
        WebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                log.info("FastAPI WebSocket 연결 성공: {}", session.getId());
                fastapiSessions.put(sessionKey, session);
                
                // 연결 완료 후 프롬프트 전송 (FastAPI의 @router.websocket("/ws-llm-stream") 호출)
                try {
                    session.sendMessage(new TextMessage(prompt));
                    log.info("FastAPI로 프롬프트 전송 완료: {}", prompt);
                } catch (Exception e) {
                    log.error("프롬프트 전송 실패: {}", e.getMessage());
                }
            }
            
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                String response = message.getPayload();
                log.info("FastAPI에서 받은 LLM 응답: {}", response);
                
                // 클라이언트에게 응답 전송 (WebSocket 핸들러를 통해)
                WebSocketSession clientSession = clientSessions.get(sessionKey);
                
                // 세션을 찾지 못한 경우 다른 키로도 검색
                if (clientSession == null || !clientSession.isOpen()) {
                    log.warn("❌ 세션 키 '{}'로 클라이언트 세션을 찾을 수 없음", sessionKey);
                    
                    // 다른 키로 검색 시도
                    for (Map.Entry<String, WebSocketSession> entry : clientSessions.entrySet()) {
                        String key = entry.getKey();
                        WebSocketSession sessionValue = entry.getValue();
                        
                        // sessionKey에서 clientId 부분 추출 (예: "llm-test-client-123" -> "test-client-123")
                        String sessionKeyClientId = sessionKey.replace("llm-", "");
                        
                        if (key.contains(sessionKeyClientId) && sessionValue.isOpen()) {
                            clientSession = sessionValue;
                            log.info("✅ 대체 키 '{}'로 클라이언트 세션 발견: {}", key, sessionValue.getId());
                            break;
                        }
                    }
                }
                
                if (clientSession != null && clientSession.isOpen()) {
                    try {
                        clientSession.sendMessage(new TextMessage(response));
                        log.info("✅ 클라이언트에게 응답 전송 성공: {}", response);
                    } catch (Exception e) {
                        log.error("❌ 클라이언트 응답 전송 실패: {}", e.getMessage());
                    }
                } else {
                    log.error("❌ 클라이언트 세션이 없거나 닫혀있음: {}", sessionKey);
                    log.error("현재 등록된 클라이언트 세션들: {}", clientSessions.keySet());
                    log.error("찾으려는 세션 키: {}", sessionKey);
                }
                
                // 스트리밍 완료 확인
                if (response.contains("[DONE]") || response.contains("스트리밍 완료")) {
                    log.info("🎉 LLM 스트리밍 완료 - FastAPI 연결 종료");
                    try {
                        session.close();
                    } catch (IOException e) {
                        log.error("FastAPI WebSocket 연결 종료 실패: {}", e.getMessage());
                    }
                }
            }
            
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                log.info("FastAPI WebSocket 연결 종료: {}", status);
                fastapiSessions.remove(sessionKey);
            }
            
            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                log.error("FastAPI WebSocket 오류: {}", exception.getMessage());
                
                // 오류 발생 시 클라이언트에게 알림
                WebSocketSession clientSession = clientSessions.get(sessionKey);
                if (clientSession != null && clientSession.isOpen()) {
                    try {
                        clientSession.sendMessage(new TextMessage("❌ FastAPI 연결 오류: " + exception.getMessage()));
                    } catch (Exception e) {
                        log.error("오류 메시지 전송 실패: {}", e.getMessage());
                    }
                }
            }
        };
        
        // FastAPI WebSocket 연결 및 LLM 스트리밍 시작 (재시도 로직 포함)
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                log.info("FastAPI WebSocket 연결 시도 {}/{}: {}", retryCount + 1, maxRetries, url);
                client.doHandshake(handler, url).get();
                log.info("FastAPI WebSocket 연결 완료 및 LLM 스트리밍 시작: {}", sessionKey);
                break;
            } catch (Exception e) {
                retryCount++;
                log.warn("FastAPI WebSocket 연결 실패 {}/{}: {}", retryCount, maxRetries, e.getMessage());
                
                if (retryCount >= maxRetries) {
                    log.error("FastAPI WebSocket 연결 최종 실패: {}", e.getMessage());
                    throw new Exception("FastAPI WebSocket 연결 실패: " + e.getMessage(), e);
                }
                
                // 재시도 전 잠시 대기
                try {
                    Thread.sleep(2000 * retryCount); // 2초, 4초, 6초 대기
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new Exception("연결 재시도 중 중단됨", ie);
                }
            }
        }
    }

    // Diagnosis 스트리밍 시작 + relay 연결
    public void startDiagnosisStreamingWithRelay(String sessionKey, String instanceUuid, String description) throws Exception {
        log.info("Starting Diagnosis streaming with relay for session: {} with description: {}", sessionKey, description);

        // FastAPI WebSocket 연결 생성
        StandardWebSocketClient client = new StandardWebSocketClient();
        String url = fastApiWsBaseUrl + "/image-meta-ws/image-meta-diagnosis-ws";

        // WebSocket 클라이언트 설정 (타임아웃 및 버퍼 크기 조정)
        Map<String, Object> properties = new HashMap<>();
        properties.put("org.apache.tomcat.websocket.IO_TIMEOUT_MS", "60000"); // 60초
        properties.put("org.apache.tomcat.websocket.WS_RCV_BUFFER_SIZE", "16384"); // 버퍼 크기
        properties.put("org.apache.tomcat.websocket.WS_SND_BUFFER_SIZE", "16384"); // 버퍼 크기
        properties.put("org.apache.tomcat.websocket.WS_CONNECT_TIMEOUT_MS", "30000"); // 연결 타임아웃
        client.setUserProperties(properties);

        WebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                log.info("FastAPI WebSocket 연결 성공: {}", session.getId());
                fastapiSessions.put(sessionKey, session);

                // 연결 완료 후 프롬프트 전송 (FastAPI의 @router.websocket("/ws-nlp-stream") 호출)
                try {
                    session.sendMessage(new TextMessage(instanceUuid));
                    log.info("FastAPI로 uuid 전송 완료: {}", instanceUuid);
                    session.sendMessage(new TextMessage(description));
                    log.info("FastAPI로 description 전송 완료: {}", description);
                } catch (Exception e) {
                    log.error("metadata 전송 실패: {}", e.getMessage());
                }
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                String response = message.getPayload();
                log.info("FastAPI에서 받은 LLM 응답: {}", response);

                // 클라이언트에게 응답 전송 (WebSocket 핸들러를 통해)
                WebSocketSession clientSession = clientSessions.get(sessionKey);

                // 세션을 찾지 못한 경우 다른 키로도 검색
                if (clientSession == null || !clientSession.isOpen()) {
                    log.warn("❌ 세션 키 '{}'로 클라이언트 세션을 찾을 수 없음", sessionKey);

                    // 다른 키로 검색 시도
                    for (Map.Entry<String, WebSocketSession> entry : clientSessions.entrySet()) {
                        String key = entry.getKey();
                        WebSocketSession sessionValue = entry.getValue();

                        // sessionKey에서 clientId 부분 추출 (예: "llm-test-client-123" -> "test-client-123")
                        String sessionKeyClientId = sessionKey.replace("llm-", "");

                        if (key.contains(sessionKeyClientId) && sessionValue.isOpen()) {
                            clientSession = sessionValue;
                            log.info("✅ 대체 키 '{}'로 클라이언트 세션 발견: {}", key, sessionValue.getId());
                            break;
                        }
                    }
                }

                if (clientSession != null && clientSession.isOpen()) {
                    try {
                        clientSession.sendMessage(new TextMessage(response));
                        log.info("✅ 클라이언트에게 응답 전송 성공: {}", response);
                    } catch (Exception e) {
                        log.error("❌ 클라이언트 응답 전송 실패: {}", e.getMessage());
                    }
                } else {
                    log.error("❌ 클라이언트 세션이 없거나 닫혀있음: {}", sessionKey);
                    log.error("현재 등록된 클라이언트 세션들: {}", clientSessions.keySet());
                    log.error("찾으려는 세션 키: {}", sessionKey);
                }

                // 스트리밍 완료 확인
                if (response.contains("[DONE]") || response.contains("스트리밍 완료")) {
                    log.info("🎉 Diagnosis 스트리밍 완료 - FastAPI 연결 종료");
                    try {
                        session.close();
                    } catch (IOException e) {
                        log.error("FastAPI WebSocket 연결 종료 실패: {}", e.getMessage());
                    }
                }
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                log.info("FastAPI WebSocket 연결 종료: {}", status);
                fastapiSessions.remove(sessionKey);
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                log.error("FastAPI WebSocket 오류: {}", exception.getMessage());

                // 오류 발생 시 클라이언트에게 알림
                WebSocketSession clientSession = clientSessions.get(sessionKey);
                if (clientSession != null && clientSession.isOpen()) {
                    try {
                        clientSession.sendMessage(new TextMessage("❌ FastAPI 연결 오류: " + exception.getMessage()));
                    } catch (Exception e) {
                        log.error("오류 메시지 전송 실패: {}", e.getMessage());
                    }
                }
            }
        };

        // FastAPI WebSocket 연결 및 Diagnosis 스트리밍 시작 (재시도 로직 포함)
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                log.info("FastAPI WebSocket 연결 시도 {}/{}: {}", retryCount + 1, maxRetries, url);
                client.doHandshake(handler, url).get();
                log.info("FastAPI WebSocket 연결 완료 및 Diagnosis 스트리밍 시작: {}", sessionKey);
                break;
            } catch (Exception e) {
                retryCount++;
                log.warn("FastAPI WebSocket 연결 실패 {}/{}: {}", retryCount, maxRetries, e.getMessage());

                if (retryCount >= maxRetries) {
                    log.error("FastAPI WebSocket 연결 최종 실패: {}", e.getMessage());
                    throw new Exception("FastAPI WebSocket 연결 실패: " + e.getMessage(), e);
                }

                // 재시도 전 잠시 대기
                try {
                    Thread.sleep(2000 * retryCount); // 2초, 4초, 6초 대기
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new Exception("연결 재시도 중 중단됨", ie);
                }
            }
        }
    }

    // 세션 상태 확인 및 관리
    public boolean isSessionActive(String key) {
        WebSocketSession session = fastapiSessions.get(key);
        return session != null && session.isOpen();
    }

    public Map<String, WebSocketSession> getActiveSessions() {
        return new ConcurrentHashMap<>(fastapiSessions);
    }
    
    public Map<String, WebSocketSession> getClientSessions() {
        return new ConcurrentHashMap<>(clientSessions);
    }

    public void cleanupInactiveSessions() {
        fastapiSessions.entrySet().removeIf(entry -> 
            entry.getValue() == null || !entry.getValue().isOpen());
        clientSessions.entrySet().removeIf(entry -> 
            entry.getValue() == null || !entry.getValue().isOpen());
    }
}

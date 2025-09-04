package com.team1.soai.service;

import com.team1.soai.dto.ChatHistory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "fastapi.websocket.url=ws://192.168.0.215:8000")
public class WebSocketAIAPIServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private WebSocketChatService webSocketChatService;

    @Autowired
    private WebSocketCaptioningService webSocketCaptioningService;

    @Mock
    private WebSocketConnectionService mockConnectionService;

    private static final String CHAT_WS_URL = "ws://192.168.0.215:8000/chat-bot/ws-llm";
    private static final String CAPTIONING_WS_URL = "ws://192.168.0.215:8000/image-meta/ws-diagnosis";

    @BeforeEach
    void setUp() {
        // WebSocket URL 강제 주입
        ReflectionTestUtils.setField(webSocketChatService, "fastApiWebSocketUrl", "ws://192.168.0.215:8000");
        ReflectionTestUtils.setField(webSocketCaptioningService, "fastApiWebSocketUrl", "ws://192.168.0.215:8000");
    }

    @Test
    public void getWebSocketChatResponse() throws Exception {
        // given
        String question = "60세 남성이 만성 요통과 다리 저림을 호소하며 내원했다. MRI에서 요추 추간판 탈출증이 확인되었다. 이 환자의 초기 치료 방침을 설명하시오.";

        // when
        webSocketChatService.connect(CHAT_WS_URL);
        String result = webSocketChatService.sendMessage(question);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).doesNotContain("Error:");

        log.info("WebSocket LLM 응답: {}", result);

        // 연결 정리
        webSocketChatService.disconnect();
    }

    @Test
    public void saveWebSocketChatResponse() throws Exception {
        // given
        String userMessage = "60세 남성이 만성 요통과 다리 저림을 호소하며 내원했다. " +
                "MRI에서 요추 추간판 탈출증이 확인되었다. " +
                "이 환자의 초기 치료 방침을 설명하시오.";

        // when: WebSocket을 통한 LLM 응답 생성
        webSocketChatService.connect(CHAT_WS_URL);
        String botResponse = webSocketChatService.sendMessage(userMessage);

        // 실제 DB 저장
        chatService.saveChatHistory(userMessage, botResponse);

        // 저장된 값 조회 (DB에서 가져오기)
        var historyList = chatService.getChatHistory();

        // 마지막 저장된 항목 검증
        ChatHistory lastHistory = historyList.get(historyList.size() - 1);


        log.info("WebSocket 채팅 히스토리 저장 완료: User={}, Bot={}", userMessage, botResponse);

        // 연결 정리
        webSocketChatService.disconnect();
    }

    @Test
    public void getWebSocketCaptioning() throws Exception {
        // given
        String instanceUUID = "b6291d44-100cffc4-28e32b4f-a746b007-0da58616";
        String description = "선천성유문협착증";

        // when
        webSocketCaptioningService.connect(CAPTIONING_WS_URL);
        String result = webSocketCaptioningService.sendCaptioningRequest(instanceUUID, description);

        log.info("WebSocket 캡셔닝 응답: {}", result);

        // 연결 정리
        webSocketCaptioningService.disconnect();
    }

    @Test
    public void testWebSocketConnectionManagement() throws Exception {
        // given
        String testMessage = "간단한 테스트 메시지입니다.";

        // when: 연결 테스트
        webSocketChatService.connect(CHAT_WS_URL);
        assertTrue(webSocketChatService.isConnected());

        // 메시지 전송 테스트
        String response = webSocketChatService.sendMessage(testMessage);
        assertThat(response).isNotNull();

        // 연결 해제 테스트
        webSocketChatService.disconnect();
        assertFalse(webSocketChatService.isConnected());

        log.info("WebSocket 연결 관리 테스트 완료");
    }

    @Test
    public void testWebSocketCaptioningConnectionManagement() throws Exception {
        // given
        String instanceUUID = "test-instance-uuid";
        String description = "정상";

        // when: 연결 테스트
        webSocketCaptioningService.connect(CAPTIONING_WS_URL);
        assertTrue(webSocketCaptioningService.isConnected());

        // 캡셔닝 요청 테스트
        String response = webSocketCaptioningService.sendCaptioningRequest(instanceUUID, description);
        assertThat(response).isNotNull();

        // 연결 해제 테스트
        webSocketCaptioningService.disconnect();
        assertFalse(webSocketCaptioningService.isConnected());

        log.info("WebSocket 캡셔닝 연결 관리 테스트 완료");
    }

    @Test
    public void testConcurrentWebSocketRequests() throws Exception {
        // given
        String[] messages = {
                "첫 번째 질문입니다.",
                "두 번째 질문입니다.",
                "세 번째 질문입니다."
        };

        // when: 동시 요청 테스트
        webSocketChatService.connect(CHAT_WS_URL);

        CompletableFuture<String>[] futures = new CompletableFuture[messages.length];
        for (int i = 0; i < messages.length; i++) {
            final String message = messages[i];
            futures[i] = CompletableFuture.supplyAsync(() -> {
                try {
                    return webSocketChatService.sendMessage(message);
                } catch (Exception e) {
                    log.error("동시 요청 중 오류 발생", e);
                    return "Error: " + e.getMessage();
                }
            });
        }

        // 모든 응답 대기
        CompletableFuture.allOf(futures).get(240, TimeUnit.SECONDS);

        // then: 결과 검증
        for (int i = 0; i < futures.length; i++) {
            String response = futures[i].get();
            assertThat(response).isNotNull();
            log.info("동시 요청 {} 응답: {}", i + 1, response);
        }

        // 연결 정리
        webSocketChatService.disconnect();
    }

    @Test
    public void testWebSocketErrorHandling() throws Exception {
        // given: 잘못된 URL로 연결 시도
        String invalidUrl = "ws://invalid-host:8000/invalid-endpoint";

        // when & then: 연결 실패 예외 처리 테스트
        assertThrows(RuntimeException.class, () -> {
            webSocketChatService.connect(invalidUrl);
        });

        log.info("WebSocket 에러 처리 테스트 완료");
    }

    @Test
    public void testWebSocketTimeout() throws Exception {
        // given
        webSocketChatService.connect(CHAT_WS_URL);

        // when: 매우 복잡한 질문으로 타임아웃 테스트 (실제로는 타임아웃되지 않을 수 있음)
        String complexQuestion = "매우 복잡하고 긴 의학적 질문을 통해 " +
                "시스템의 응답 시간과 타임아웃 처리를 테스트합니다. " +
                "이 질문은 WebSocket 연결의 안정성과 타임아웃 처리 능력을 확인하기 위한 것입니다.";

        String response = webSocketChatService.sendMessage(complexQuestion);

        // then
        assertThat(response).isNotNull();
        log.info("WebSocket 타임아웃 테스트 응답: {}", response);

        // 연결 정리
        webSocketChatService.disconnect();
    }
}
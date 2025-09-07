package com.team1.soai.service;

import com.team1.soai.dto.ChatHistory;
import com.team1.soai.dto.ChatRequest;
import com.team1.soai.dto.SeriesCardDTO;
import com.team1.soai.mapper.ChatHistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import javax.swing.text.Segment;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Exchanger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = "fastapi.url=http://192.168.0.45:8000")
public class AIAPIServiceTest {
    @Autowired
    private ChatService chatService;

    @Mock
    private ChatHistoryMapper chatHistoryMapper;

    @InjectMocks
    private SegmentationService segmentationService;

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private CaptioningService captioningService;

    @BeforeEach
    void setUp() {
        // fastApiUrl 강제 주입
        ReflectionTestUtils.setField(segmentationService, "fastApiUrl", "http://localhost:8000");
    }
    @Test
    public void getChatResponse() throws Exception{
        String question = "60세 남성이 만성 요통과 다리 저림을 호소하며 내원했다. MRI에서 요추 추간판 탈출증이 확인되었다. 이 환자의 초기 치료 방침을 설명하시오.";

        ChatRequest chatRequest = new ChatRequest(question);

        String result = chatService.getChatResponse(chatRequest);

        // then
        assertThat(result).isNotNull();
        System.out.println("LLM 응답: " + result);
    }

    @Test
    public void saveChatResponse() throws Exception{
        String userMessage = "60세 남성이 만성 요통과 다리 저림을 호소하며 내원했다. "
                + "MRI에서 요추 추간판 탈출증이 확인되었다. "
                + "이 환자의 초기 치료 방침을 설명하시오.";
        ChatRequest chatRequest = new ChatRequest(userMessage);

        // when : LLM 응답 생성
        String botResponse = chatService.getChatResponse(chatRequest);

        // then : ChatHistory 엔티티 생성 및 저장
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setUserMessage(userMessage);
        chatHistory.setBotResponse(botResponse);
        chatHistory.setModelName("Gemma3-1b"); // 모델명 기록
        chatHistory.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // 실제 DB 저장
        chatService.saveChatHistory(userMessage, botResponse);

        // 저장된 값 조회 (DB에서 가져오기)
        var historyList = chatService.getChatHistory();
        assertNotNull(historyList);
        assertFalse(historyList.isEmpty());
    }

    @Test
    public void getSegmentationImage() throws Exception {
        byte[] fakeImage = "fake-image".getBytes();
        ResponseEntity<byte[]> fakeResponse = new ResponseEntity<>(fakeImage, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://192.168.0.45:8000/image/segmentation"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(byte[].class)
        )).thenReturn(fakeResponse);

        String result = segmentationService.getSegmentationImage("b6291d44-100cffc4-28e32b4f-a746b007-0da58616");
        System.out.println(result); // Base64 확인
        assertNotNull(result);
    }

    @Test
    public void testGetSegmentationArray() {
        String instanceUUID = "b6291d44-100cffc4-28e32b4f-a746b007-0da58616";

        // hex 데이터 생성 (예: 512x512 배열을 0으로 채움 → hex 문자열)
        byte[] bytes = new byte[512 * 512];
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("data", hex.toString());

        // RestTemplate Mock
        when(restTemplate.exchange(
                eq("http://192.168.0.45:8000/image/segmentation_array"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // 실제 메서드 호출
        Map<String, Object> result = segmentationService.getSegmentationArray(instanceUUID);

        assertNotNull(result);
        assertTrue(result.containsKey("data"));

        int[][] arr = (int[][]) result.get("data");
        assertEquals(512, arr.length);
        assertEquals(512, arr[0].length);

        System.out.println("Segmentation Array Sample:");
        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

    @Test
    public void getCaptioning() throws Exception {
        String instanceUUID = "b6291d44-100cffc4-28e32b4f-a746b007-0da58616";
        String description = "선천성유문협착증";
        String transcription = "";
        ResponseEntity<String> fakeResponse = new ResponseEntity<>(transcription, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://192.168.0.45:8000/image-meta/diagnosis"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(fakeResponse);

        String result = captioningService.getCaptioning(instanceUUID, description);
        System.out.println(result);
        assertNotNull(result);
    }
}

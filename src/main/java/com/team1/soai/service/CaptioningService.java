package com.team1.soai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.team1.soai.dto.CaptioningResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class CaptioningService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fastapi.url:http://localhost:8000}")
    private String fastApiUrl;

    public String getCaptioning(String studyId) {
        try {
            String url = fastApiUrl + "/image-meta/diagnosis";
            // 요청 Body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("study", studyId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // FASTAPI 호출
            ResponseEntity<CaptioningResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    CaptioningResponse.class
            );
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getTranscript();
            } else {
                throw new RuntimeException("FASTAPI 응답 실패" + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("FASTAPI 호출 중 오류 발생", e);
        }
    }
}
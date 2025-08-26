package com.team1.soai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SegmentationService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fastapi.url:http://localhost:8000}")
    private String fastApiUrl;

    public String getSegmentationImage(String studyId) {
        try {
            String url = fastApiUrl + "/image/segmentation";
            // 요청 Body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("study", studyId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // FASTAPI 호출
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    byte[].class
            );

            // Base64 변환
            String base64Image = Base64.getEncoder().encodeToString(response.getBody());
            return "data:image/png;base64," + base64Image;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

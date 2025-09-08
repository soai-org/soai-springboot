package com.team1.soai.service;

import com.team1.soai.dto.AppendicitisRequest;
import com.team1.soai.dto.AppendicitisResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppendicitisDiagnosisService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fastapi.url}")
    private String fastApiUrl;

    public AppendicitisResponse getAppendicitisResponse(AppendicitisRequest request) {
        try {
            String url = fastApiUrl + "/appendicitis/diagnosis";
            HttpHeaders headers = new HttpHeaders();
            Map<String, List<String>> requestBody = new HashMap<>();
            requestBody.put("AppendicitisUuidList", request.getAppendicitisUuidList());
            HttpEntity<Map<String, List<String>>> entity = new HttpEntity<>(requestBody, headers);
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<AppendicitisResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AppendicitisResponse.class
            );
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("FASTAPI 응답 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("FASTAPI 호출 중 오류 발생", e);
        }
    }
}

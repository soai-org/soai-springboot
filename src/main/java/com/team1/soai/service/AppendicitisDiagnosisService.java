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
import org.springframework.web.client.RestTemplate;

@Service
public class AppendicitisDiagnosisService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fastapi.url}")
    private String fastApiUrl;

    public AppendicitisResponse getAppendicitisResponse(AppendicitisRequest request) {
        try {
            String url = fastApiUrl + "/appendicitis/diagnosis";

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<AppendicitisRequest> entity = new HttpEntity<>(request, headers);

            // FASTAPI 호출
            ResponseEntity<AppendicitisResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AppendicitisResponse.class
            );
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
            else{
                throw new RuntimeException("FastAPI 응답 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("FASTAPI 호출 중 오류 발생", e);
        }
    }
}

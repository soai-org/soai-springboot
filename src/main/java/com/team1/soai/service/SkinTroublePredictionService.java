package com.team1.soai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class SkinTroublePredictionService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fastapi.url}")
    private String fastApiUrl;

    public Map<String, Object> predictSkinTrouble(String instanceUuid){
        String url = fastApiUrl + "/skin/prediction";

        //헤더 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("skin_trouble_uuid_list", instanceUuid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody,headers);

        // FASTAPI 호출
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        return response.getBody();
    }
}

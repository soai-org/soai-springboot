package com.team1.soai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrthancService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${orthanc.endpoint}")
    private String orthancEndpoint;

    @Value("${orthanc.id}")
    private String orthancID;
    @Value("${orthanc.password}")
    private String orthancPassword;

    public String toolsFind(String level, Map<String, Object> query) {
        String url = orthancEndpoint + "/tools/find";

        Map<String, Object> body = new HashMap<>();
        body.put("Level", level);
        body.put("Query", query);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String auth = orthancID + ":" + orthancPassword;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        return response.getBody();
    }
}

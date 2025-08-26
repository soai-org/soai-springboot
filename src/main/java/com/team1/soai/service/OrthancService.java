package com.team1.soai.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrthancService {
    private final RestTemplate restTemplate;
    private final String orthancEndpoint;
    private final String orthancID;
    private final String orthancPassword;

    public OrthancService(
            RestTemplate restTemplate,
            @Value("${orthanc.endpoint}") String orthancEndpoint,
            @Value("${orthanc.id}") String orthancID,
            @Value("${orthanc.password}") String orthancPassword) {
        this.restTemplate = restTemplate;
        this.orthancEndpoint = orthancEndpoint;
        this.orthancID = orthancID;
        this.orthancPassword = orthancPassword;
    }

    private String getAuthHeader() {
        String auth = orthancID + ":" + orthancPassword;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }

    public List<String> toolsFind(String level, Map<String, Object> query) {
        String url = orthancEndpoint + "/tools/find"; 
        Map<String, Object> body = new HashMap<>();
        body.put("Level", level);
        body.put("Query", query);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthHeader());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers); 
        ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
        return response.getBody();
    }

    public String getDetail(String levelPath, String id) {
        String url = orthancEndpoint + "/" + levelPath + "/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAuthHeader());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }

    public List<String> toolsFindFull(String level, Map<String, Object> query) {
        String url = orthancEndpoint + "/tools/find";
        Map<String, Object> body = new HashMap<>();
        body.put("Level", level);
        body.put("Query", query);
        body.put("Full", true);
        body.put("Expand", true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthHeader());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
        return response.getBody();
    }


}
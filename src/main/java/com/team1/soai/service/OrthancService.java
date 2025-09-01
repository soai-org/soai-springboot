package com.team1.soai.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.soai.dto.Level;
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

    public List<String> toolsFindByParentPatient(String level, Map<String, Object> query, String parentPatient) {
        String url = orthancEndpoint + "/tools/find";
        Map<String, Object> body = new HashMap<>();
        body.put("Level", level);
        body.put("Query", query);
        body.put("ParentPatient", parentPatient);

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

    public List<String> toolsFindExpand(String level, Map<String, Object> query) {
        String url = orthancEndpoint + "/tools/find";
        Map<String, Object> body = new HashMap<>();
        body.put("Level", level);
        body.put("Query", query);
        body.put("Expand", true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthHeader());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
        return response.getBody();
    }

    public List<Map<String, Object>> toolsFindRequestedTagsByParentPatient(String level, String PatientUuid) {
        String url = orthancEndpoint + "/tools/find";
        Map<String, Object> body = new HashMap<>();
        body.put("Level", level);
        body.put("Query", Map.of());
        body.put("ParentPatient", PatientUuid);
        body.put("ResponseContent", List.of());
        body.put("RequestedTags", List.of("StudyDate"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthHeader());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
        return response.getBody();
    }

    public List<Map<String, Object>> toolsFindStudyForPaginationByPatientUuid(int limit, int since, String parentPatient) {
        String url = orthancEndpoint + "/tools/find";
        Map<String, Object> body = new HashMap<>();
        body.put("Level", "Study");
        body.put("Limit", limit);
        body.put("Since", since);
        body.put("Query", Map.of());
        body.put("ParentPatient", parentPatient);
        body.put("ResponseContent", List.of());
        body.put("RequestedTags", List.of(
            "StudyDate",
            "StudyTime", 
            "StudyDescription",
            "PatientName",
            "PatientSex"
        ));
        body.put("OrderBy", List.of(Map.of(
            "Type", "DicomTags",
            "Key", "StudyDate", 
            "Direction", "DESC"
        )));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthHeader());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
        return response.getBody();
    }

    public List<Map<String, Object>> toolsFindSeriesByStudyUuid(String ParentStudy) throws JsonProcessingException {
        String url = orthancEndpoint + "/tools/find";
        Map<String, Object> body = new HashMap<>();
        body.put("Level", Level.Series);
        body.put("Query", Map.of());
        body.put("ParentStudy", ParentStudy);
        body.put("ResponseContent", List.of("Children", "MainDicomTags"));

        ObjectMapper mapper = new ObjectMapper();
        log.info("Request body: {}", mapper.writeValueAsString(body));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthHeader());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
        return response.getBody();
    }

    public List<String> toolsFindInstanceBySeriesUuid(String ParentSeries) {
        String url = orthancEndpoint + "/tools/find";
        Map<String, Object> body = new HashMap<>();
        body.put("Level", Level.Instance);
        body.put("Query", Map.of());
        body.put("ParentSeries", ParentSeries);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthHeader());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
        return response.getBody();
    }

    public byte[] getDicomFilByByte(String instanceUuid) throws Exception{
        String url = orthancEndpoint + "/instances/" + instanceUuid + "/file";
        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAuthHeader());
        HttpEntity entity = new HttpEntity(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        return response.getBody();
    }

    public String getThumbnailImageAsBase64(String instanceUuid) {
        String url = orthancEndpoint + "/instances/" + instanceUuid + "/preview";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthHeader());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        byte[] imageBytes = response.getBody();
        String contentType = response.getHeaders().getContentType().toString();

        return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
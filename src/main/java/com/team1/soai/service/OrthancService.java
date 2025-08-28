package com.team1.soai.service;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.springframework.beans.factory.annotation.Value;
import org.dcm4che3.io.DicomInputStream;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
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

    public List<String> toolsFindByPatentPatient(String level, Map<String, Object> query, String parentPatient) {
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
        body.put("Full", true);
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

    public byte[] getDicomFilByByte(String instanceUuid) throws Exception{
        String url = orthancEndpoint + "/instances/" + instanceUuid + "/file";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(url, byte[].class);

        return restTemplate.getForObject(url, byte[].class);
    }

    public byte[] getThumbnailImageAsBytes(String instanceUuid) {
        String url = orthancEndpoint + "/instances/" + instanceUuid + "/preview";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, byte[].class);
    }
}
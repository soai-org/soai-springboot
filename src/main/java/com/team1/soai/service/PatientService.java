package com.team1.soai.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import com.team1.soai.dto.PatientDataResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {

    private final String ORTHANC_URL = "http://127.0.0.1:8042";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 모든 환자 UUID 가져오기
    public List<String> getAllPatients() throws Exception {
        String url = ORTHANC_URL + "/patients";
        String response = restTemplate.getForObject(url, String.class);
        return objectMapper.readValue(response, List.class);
    }

    // 해당 환자의 이름을 검색해서 관련정보를 파싱하고, Studies를 파싱한다.
    public List<PatientDataResponse> getPatientsByName(String patientName) throws Exception {
        List<String> allPatients = getAllPatients();
        List<PatientDataResponse> result = new ArrayList<>();

        for (String uuid : allPatients) {
            String url = ORTHANC_URL + "/patients/" + uuid;
            String patientJson = restTemplate.getForObject(url, String.class);
            JsonNode node = objectMapper.readTree(patientJson).get("MainDicomTags");

            if (node != null && patientName.equals(node.get("PatientName").asText())) {
                List<String> studies = new ArrayList<>();
                JsonNode studiesNode = objectMapper.readTree(patientJson).get("Studies");
                if (studiesNode.isArray()) {
                    for (JsonNode s : studiesNode) {
                        studies.add(s.asText());
                    }
                }

                result.add(new PatientDataResponse(
                        node.get("PatientID").asText(),
                        node.get("PatientName").asText(),
                        node.get("PatientBirthDate").asText(),
                        node.get("PatientSex").asText(),
                        studies
                ));
            }
        }
        return result; // 여러 명 반환;
    }
    public Map<String, List<String>> getSeriesByStudyIds(List<String> studyIds) throws Exception {
        Map<String, List<String>> result = new HashMap<>();

        for (String studyId : studyIds) {
            String url = ORTHANC_URL + "/studies/" + studyId;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode seriesNode = objectMapper.readTree(response).get("Series");

            List<String> seriesList = new ArrayList<>();
            if (seriesNode != null && seriesNode.isArray()) {
                for (JsonNode s : seriesNode) {
                    seriesList.add(s.asText());
                }
            }

            result.put(studyId, seriesList);
        }

        return result; // {studyId: [seriesId1, seriesId2, ...]}
    }
    // Series → Instance mapping
    public Map<String, List<String>> getInstancesBySeriesIds(List<String> seriesIds) throws Exception {
        Map<String, List<String>> result = new HashMap<>();

        for (String seriesId : seriesIds) {
            String url = ORTHANC_URL + "/series/" + seriesId;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode instancesNode = objectMapper.readTree(response).get("Instances");

            List<String> instanceList = new ArrayList<>();
            if (instancesNode != null && instancesNode.isArray()) {
                for (JsonNode i : instancesNode) {
                    instanceList.add(i.asText());
                }
            }
            result.put(seriesId, instanceList);
        }

        return result; // {seriesId: [instanceId1, instanceId2, ...]}
    }

    // Instance → 이미지 URL
    public String getInstancePreviewUrl(String instanceId) {
        return ORTHANC_URL + "/instances/" + instanceId + "/preview";
    }
}

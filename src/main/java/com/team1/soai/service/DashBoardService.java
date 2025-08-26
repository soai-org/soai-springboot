package com.team1.soai.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.soai.dto.DicomTag;
import com.team1.soai.dto.FindLvPatientDTO;
import com.team1.soai.dto.FindLvStudyDTO;
import com.team1.soai.dto.FindLvSeriesDTO;
import com.team1.soai.dto.FindLvInstanceDTO;
import com.team1.soai.dto.Level;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.team1.soai.dto.PageResult;
import com.team1.soai.dto.StudyCardDTO;

@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final OrthancService orthancService;
    private final ObjectMapper objectMapper;

    /** 이름 검색 (Full=false) */
    public List<?> toolsFind(String name, Level level) throws JsonProcessingException {
        Map<String, Object> query = Map.of("PatientName", name);
        List<String> uuids = orthancService.toolsFind(level.getValue(), query);
        return fetchDetails(uuids, level);
    }

    /** 이름 검색 (Full=true) */
    public List<?> toolsFindFull(String name, Level level) throws JsonProcessingException {
        Map<String, Object> query = Map.of("PatientName", name);
        // Full=true 호출, Orthanc가 이미 상세 JSON 반환
        List<?> fullList = orthancService.toolsFindFull(level.getValue(), query);
        return fullList; // fetchDetails 생략
    }

    private List<?> fetchDetails(List<String> uuids, Level level) throws JsonProcessingException {
        List<Object> result = new ArrayList<>();
        for (String id : uuids) {
            String json;
            switch (level) {
                case Patient -> {
                    json = orthancService.getDetail("patients", id);
                    result.add(objectMapper.readValue(json, FindLvPatientDTO.class));
                }
                case Study -> {
                    json = orthancService.getDetail("studies", id);
                    result.add(objectMapper.readValue(json, FindLvStudyDTO.class));
                }
                case Series -> {
                    json = orthancService.getDetail("series", id);
                    result.add(objectMapper.readValue(json, FindLvSeriesDTO.class));
                }
                case Instance -> {
                    json = orthancService.getDetail("instances", id);
                    result.add(objectMapper.readValue(json, FindLvInstanceDTO.class));
                }
            }
        }
        return result;
    }

    /** Patient ID로 Study 목록을 페이지네이션하여 반환 */
    public PageResult<StudyCardDTO> getPatientStudiesWithPagination(String patientId, int page, int size, String sortBy, String sortOrder) throws JsonProcessingException {
        // 1. Patient ID로 해당 Patient의 Study UUID 목록 조회
        Map<String, Object> query = Map.of("PatientID", patientId);
        List<String> studyUuids = orthancService.toolsFind("Study", query);
        
        // 2. 전체 개수 계산
        int totalCount = studyUuids.size();
        
        // 3. 페이지네이션 적용
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalCount);
        
        if (startIndex >= totalCount) {
            return new PageResult<>(List.of(), page, size, totalCount);
        }
        
        // 4. 해당 페이지의 Study UUID들만 추출
        List<String> pageUuids = studyUuids.subList(startIndex, endIndex);
        
        // 5. 각 Study의 상세 정보 조회하여 StudyCardDTO로 변환
        List<StudyCardDTO> studies = new ArrayList<>();
        for (String uuid : pageUuids) {
            String json = orthancService.getDetail("studies", uuid);
            Map<String, Object> studyData = objectMapper.readValue(json, Map.class);
            
            StudyCardDTO studyCard = new StudyCardDTO();
            studyCard.setStudyUuid(uuid);
            
            // MainDicomTags에서 정보 추출
            Map<String, Object> mainTags = (Map<String, Object>) studyData.get("MainDicomTags");
            if (mainTags != null) {
                studyCard.setStudyDate((String) mainTags.get("StudyDate"));
                studyCard.setStudyDescription((String) mainTags.get("StudyDescription"));
                studyCard.setModality((String) mainTags.get("Modality"));
            }
            
            // Patient 정보 추출
            Map<String, Object> patientMainTags = (Map<String, Object>) studyData.get("PatientMainDicomTags");
            if (patientMainTags != null) {
                studyCard.setPatientId((String) patientMainTags.get("PatientID"));
                studyCard.setPatientName((String) patientMainTags.get("PatientName"));
            }
            
            // Series/Instance 개수 추출
            List<String> seriesUuids = (List<String>) studyData.get("Series");
            studyCard.setSeriesCount(seriesUuids != null ? seriesUuids.size() : 0);
            
            // 실제로는 PATIENT_LATEST_DATA 테이블에서 thumbnailInstanceUuid를 가져와야 함
            // 현재는 임시로 첫 번째 Series의 첫 번째 Instance를 사용
            if (seriesUuids != null && !seriesUuids.isEmpty()) {
                String firstSeriesJson = orthancService.getDetail("series", seriesUuids.get(0));
                Map<String, Object> firstSeriesData = objectMapper.readValue(firstSeriesJson, Map.class);
                List<String> instanceUuids = (List<String>) firstSeriesData.get("Instances");
                if (instanceUuids != null && !instanceUuids.isEmpty()) {
                    studyCard.setThumbnailInstanceUuid(instanceUuids.get(0));
                }
                studyCard.setInstanceCount(instanceUuids != null ? instanceUuids.size() : 0);
            }
            
            studies.add(studyCard);
        }
        
        return new PageResult<>(studies, page, size, totalCount);
    }

}

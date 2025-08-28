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
import com.team1.soai.mapper.UuidMappingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import com.team1.soai.dto.PageResult;
import com.team1.soai.dto.StudyCardDTO;

@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final OrthancService orthancService;
    private final ObjectMapper objectMapper;
    private final UuidMappingMapper uuidMappingMapper;

    /** 이름 검색 (Expand=false) */
    public List<?> toolsFind(String name, Level level) throws JsonProcessingException {
        Map<String, Object> query = Map.of("PatientName", name);
        List<String> uuids = orthancService.toolsFind(level.getValue(), query);
        return fetchDetails(uuids, level);
    }

    /** 이름 검색 (Expand=true) */
    public List<?> toolsFindExpand(String name, Level level) throws JsonProcessingException {
        Map<String, Object> query = Map.of("PatientName", name);
        // Expanded=true 호출, Orthanc가 이미 상세 JSON 반환
        List<?> expandedList = orthancService.toolsFindExpand(level.getValue(), query);
        return expandedList; // fetchDetails 생략
    }

    /** Patient uuid로 study 목록 반환 */
    public List<?> toolsFindByPatientId(String name, Level level, String uuid) throws JsonProcessingException {
        Map<String, Object> query = Map.of("PatientName", name);
        List<String> List = orthancService.toolsFindByParentPatient(level.getValue(), query, uuid);

        return fetchDetails(List, Level.Study);
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

    public List<StudyCardDTO> studiesPagination(String level, String PatientUuid, int size, int page) {
        List<Map<String, Object>> fullList = orthancService.toolsFindRequestedTagsByParentPatient(level, PatientUuid);

        fullList.sort((a, b) -> {
            Map<String, String> tagsA = (Map<String, String>) a.get("RequestedTags");
            Map<String, String> tagsB = (Map<String, String>) b.get("RequestedTags");

            String dateA = tagsA.get("StudyDate");
            String dateB = tagsB.get("StudyDate");

            return dateB.compareTo(dateA); // DESC 정렬
        });

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, fullList.size());

        if (fromIndex >= fullList.size()) {
            return Collections.emptyList(); // 페이지 범위 초과 시 빈 리스트
        }

        List<Map<String, Object>> paginatedList = fullList.subList(fromIndex, toIndex);

        List<StudyCardDTO> result = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        // 4. 각 Study 상세 조회 후 DTO 생성
        for (Map<String, Object> item : paginatedList) {
            String id = (String) item.get("ID");

            // 상세 API 호출
            String detailJson = orthancService.getDetail("studies", id);

            // JSON → Map 변환
            Map<String, Object> detailMap;
            try {
                detailMap = mapper.readValue(detailJson, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse study detail JSON", e);
            }

            // 필드 추출
            String studyDate = ((Map<String, Object>) item.get("RequestedTags")).get("StudyDate").toString();
            String patientName = ((Map<String, Object>) ((Map<String, Object>) detailMap.get("PatientMainDicomTags"))).get("PatientName").toString();
            String studyDescription = ((Map<String, Object>) ((Map<String, Object>) detailMap.get("MainDicomTags"))).get("StudyDescription").toString();

            // DTO 생성 후 리스트에 추가
            result.add(new StudyCardDTO(id, studyDate, patientName, studyDescription));
        }

        // 5. 최종 반환
        return result;
    }

    public List<StudyCardDTO> getStudyCardList(int limit, int since, String parentPatient) {
        try {
            List<Map<String, Object>> studyList = orthancService.toolsFindStudyForPaginationByPatientUuid(limit, since, parentPatient);
            
            List<StudyCardDTO> result = new ArrayList<>();
            
            for (Map<String, Object> study : studyList) {
                String studyUuid = (String) study.get("ID");
                
                Map<String, Object> requestedTags = (Map<String, Object>) study.get("RequestedTags");
                String studyDate = requestedTags.get("StudyDate") != null ? requestedTags.get("StudyDate").toString() : "";
                String studyTime = requestedTags.get("StudyTime") != null ? requestedTags.get("StudyTime").toString() : "";
                String studyDescription = requestedTags.get("StudyDescription") != null ? requestedTags.get("StudyDescription").toString() : "";
                String patientName = requestedTags.get("PatientName") != null ? requestedTags.get("PatientName").toString() : "";
                String patientSex = requestedTags.get("PatientSex") != null ? requestedTags.get("PatientSex").toString() : "";
                
                StudyCardDTO studyCard = new StudyCardDTO();
                studyCard.setStudyUuid(studyUuid);
                studyCard.setStudyDate(studyDate);
                studyCard.setStudyTime(studyTime);
                studyCard.setStudyDescription(studyDescription);
                studyCard.setThumbnailImage(
                        orthancService.getThumbnailImageAsBytes(
                                uuidMappingMapper.getLatestInstanceUuidByStudyUuid(studyUuid)
                        ));
                studyCard.setPatientName(patientName);
                studyCard.setPatientSex(patientSex);
                
                result.add(studyCard);
            }
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to get study card list", e);
        }
    }
}

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

@Service 
@RequiredArgsConstructor
public class DashBoardService {
    private final OrthancService orthancService;
    private final ObjectMapper objectMapper;

    public Object findByName(String name, Level level) throws JsonProcessingException {
        OrthancQueryBuilder builder = new OrthancQueryBuilder();

        Map<String, Object> query = builder.add(DicomTag.PatientName, name).build();

        List<String> uuids = orthancService.toolsFind(level.getValue(), query);

        List<Object> result = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        switch (level) {
            case Patient:
                for (String id : uuids) {
                    String json = orthancService.getDetail("patients", id);
                    FindLvPatientDTO dto = mapper.readValue(json, FindLvPatientDTO.class);
                    result.add(dto);
                }
                break;

            case Study:
                for (String id : uuids) {
                    String json = orthancService.getDetail("studies", id);
                    FindLvStudyDTO dto = mapper.readValue(json, FindLvStudyDTO.class);
                    result.add(dto);
                }
                break;

            case Series:
                for (String id : uuids) {
                    String json = orthancService.getDetail("series", id);
                    FindLvSeriesDTO dto = mapper.readValue(json, FindLvSeriesDTO.class);
                    result.add(dto);
                }
                break;

            case Instance:
                for (String id : uuids) {
                    String json = orthancService.getDetail("instances", id);
                    FindLvInstanceDTO dto = mapper.readValue(json, FindLvInstanceDTO.class);
                    result.add(dto);
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported level: " + level);
        }

        return result;
    }
}
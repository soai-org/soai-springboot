package com.team1.soai.service;

import com.team1.soai.dto.SeriesCardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ViewerService {
    private final OrthancService orthancService;

    public List<String> getInstanceList(String ParentSeries) {
        return orthancService.toolsFindInstanceBySeriesUuid(ParentSeries);
    }

    public byte[] getDicomData(String instanceUuid) throws Exception{
        return orthancService.getDicomFilByByte(instanceUuid);
    }

    public List<SeriesCardDTO> getSeriesList(String studyUuid) throws Exception{
        List<Map<String, Object>> SeriesInstanceMapList = orthancService.toolsFindSeriesByStudyUuid(studyUuid);

        List<SeriesCardDTO> result = new ArrayList<>();

        // Map → DTO 변환
        try {
            for (Map<String, Object> seriesMap : SeriesInstanceMapList) {
                String id = (String) seriesMap.get("ID");
                List<String> instances = (List<String>) seriesMap.get("Instances");
                Map<String, Object> mainDicomTagsMap = (Map<String, Object>) seriesMap.get("MainDicomTags");

                SeriesCardDTO.MainDicomTags mainDicomTags = new SeriesCardDTO.MainDicomTags(
                        (String) mainDicomTagsMap.get("Modality"),
                        (String) mainDicomTagsMap.get("SeriesInstanceUID")
                );

                SeriesCardDTO dto = new SeriesCardDTO();
                dto.setId(id);
                dto.setInstances(instances);
                dto.setMainDicomTags(mainDicomTags);
                dto.setThumbnailImage(
                        orthancService.getThumbnailImageAsBase64(
                                instances.get(0)
                        ));

                result.add(dto);
            }

            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}

package com.team1.soai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeriesCardDTO {
    private String id;
    private List<String> instances;
    private MainDicomTags mainDicomTags;
    private String thumbnailImage;

    @Data
    @AllArgsConstructor
    public static class MainDicomTags {
        private String modality;
        private String seriesInstanceUID;
    }
}

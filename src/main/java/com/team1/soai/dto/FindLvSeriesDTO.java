package com.team1.soai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindLvSeriesDTO {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("ExpectedNumberOfInstances")
    private Integer expectedNumberOfInstances;

    @JsonProperty("Instances")
    private List<String> instances;

    @JsonProperty("IsStable")
    private Boolean isStable;

    @JsonProperty("Labels")
    private List<String> labels;

    @JsonProperty("LastUpdate")
    private String lastUpdate;

    @JsonProperty("MainDicomTags")
    private SeriesDicomTags mainDicomTags;

    @JsonProperty("ParentStudy")
    private String parentStudy;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Type")
    private String type;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SeriesDicomTags {
        @JsonProperty("Modality")
        private String modality;

        @JsonProperty("SeriesInstanceUID")
        private String seriesInstanceUID;
    }
}

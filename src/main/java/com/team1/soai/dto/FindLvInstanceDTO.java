package com.team1.soai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindLvInstanceDTO {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("FileSize")
    private Long fileSize;

    @JsonProperty("FileUuid")
    private String fileUuid;

    @JsonProperty("IndexInSeries")
    private Integer indexInSeries;

    @JsonProperty("Labels")
    private List<String> labels;

    @JsonProperty("MainDicomTags")
    private InstanceDicomTags mainDicomTags;

    @JsonProperty("ParentSeries")
    private String parentSeries;

    @JsonProperty("Type")
    private String type;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InstanceDicomTags {
        @JsonProperty("SOPInstanceUID")
        private String sopInstanceUID;
    }
}
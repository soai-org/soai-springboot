package com.team1.soai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FindLvPatientDTO {
    @JsonProperty("ID")
    private String id;

    @JsonProperty("MainDicomTags")
    private MainDicomTagsDTO mainDicomTags;
}


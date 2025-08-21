package com.team1.soai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MainDicomTagsDTO {
    @JsonProperty("PatientName")
    private String patientName;

    @JsonProperty("PatientID")
    private String patientID;
}
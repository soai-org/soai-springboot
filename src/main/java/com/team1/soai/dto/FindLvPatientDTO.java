package com.team1.soai.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindLvPatientDTO {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("IsProtected")
    private Boolean isProtected;

    @JsonProperty("IsStable")
    private Boolean isStable;

    @JsonProperty("Labels")
    private List<String> labels;

    @JsonProperty("LastUpdate")
    private String lastUpdate;

    @JsonProperty("MainDicomTags")
    private PatientDicomTags mainDicomTags;

    @JsonProperty("Studies")
    private List<String> studies;

    @JsonProperty("Type")
    private String type;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PatientDicomTags {
        @JsonProperty("PatientName")
        private String patientName;

        @JsonProperty("PatientID")
        private String patientID;

        @JsonProperty("PatientBirthDate")
        private String patientBirthDate;

        @JsonProperty("PatientSex")
        private String patientSex;
    }
}
package com.team1.soai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindLvStudyDTO {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("IsStable")
    private Boolean isStable;

    @JsonProperty("Labels")
    private List<String> labels;

    @JsonProperty("LastUpdate")
    private String lastUpdate;

    @JsonProperty("ParentPatient")
    private String parentPatient;

    @JsonProperty("PatientMainDicomTags")
    private PatientDicomTags patientMainDicomTags;

    @JsonProperty("MainDicomTags")
    private StudyDicomTags mainDicomTags;

    @JsonProperty("Series")
    private List<String> series;

    @JsonProperty("Type")
    private String type;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StudyDicomTags {
        @JsonProperty("StudyDate")
        private String studyDate;

        @JsonProperty("StudyDescription")
        private String studyDescription;

        @JsonProperty("StudyInstanceUID")
        private String studyInstanceUID;

        @JsonProperty("StudyTime")
        private String studyTime;
    }

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
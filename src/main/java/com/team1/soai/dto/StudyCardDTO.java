package com.team1.soai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyCardDTO {
    private String studyUuid;
    private String studyDate;
    private String studyTime;
    private String studyDescription;

    private String thumbnailImage;
    private String patientId;
    private String patientName;
    private String patientSex;
    
    public StudyCardDTO(String studyUuid, String studyDate, String patientName, String studyDescription) {
        this.studyUuid = studyUuid;
        this.studyDate = studyDate;
        this.patientName = patientName;
        this.studyDescription = studyDescription;
    }
}

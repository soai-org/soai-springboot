package com.team1.soai.dto;

import lombok.Data;

@Data
public class StudyCardDTO {
    private String studyUuid;
    private String studyDate;
    private String studyDescription;
    private String modality;
    private int seriesCount;
    private int instanceCount;
    private String thumbnailInstanceUuid; // 썸네일용 Instance UUID
    private String patientId;
    private String patientName;
}

package com.team1.soai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDataResponse {
    private String patientId;
    private String patientName;
    private String patientBirthDate;
    private String patientSex;
    private List<String> studyIds;
}


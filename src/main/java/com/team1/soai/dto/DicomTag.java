package com.team1.soai.dto;

import java.util.Map;
public enum DicomTag {
    // Patient 레벨
    PatientID(String.class),
    PatientName(String.class),
    PatientBirthDate(String.class), // YYYYMMDD 문자열
    PatientSex(String.class),

    // Study 레벨
    StudyInstanceUID(String.class),
    StudyDate(Map.class), // {"From":"YYYYMMDD", "To":"YYYYMMDD"}
    StudyTime(Map.class), // {"From":"HHMMSS", "To":"HHMMSS"}
    Modality(Object.class), // String or List<String>

    // Series 레벨
    SeriesInstanceUID(String.class),
    SeriesNumber(String.class),

    // Instance 레벨
    SOPInstanceUID(String.class),
    InstanceNumber(String.class);

    private final Class<?> valueType;

    DicomTag(Class<?> valueType) {
        this.valueType = valueType;
    }
    public Class<?> getValueType() {
        return valueType;
    }
}
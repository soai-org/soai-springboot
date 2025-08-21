package com.team1.soai.dto;

import java.util.Map;

public enum DicomTag {
    // Orthanc /tools/find/ 매개변수 제약 enum

    // Patient
    PatientID(String.class),
    PatientName(String.class),
    PatientBirthDate(String.class), // YYYYMMDD 문자열
    PatientSex(String.class),

    // Study
    StudyInstanceUID(String.class),
    StudyDate(Map.class), // {"From":"YYYYMMDD", "To":"YYYYMMDD"}
    StudyTime(Map.class), // {"From":"HHMMSS", "To":"HHMMSS"}
    Modality(Object.class), // String or List<String>

    // Series
    SeriesInstanceUID(String.class),
    SeriesNumber(String.class),

    // Instance
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

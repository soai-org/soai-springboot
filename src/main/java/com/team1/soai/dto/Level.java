package com.team1.soai.dto;

import lombok.Getter;

@Getter
public enum Level {
    Patient("Patient"),
    Study("Study"),
    Series("Series"),
    Instance("Instance");

    private final String value;

    Level(String value) {
        this.value = value;
    }
}

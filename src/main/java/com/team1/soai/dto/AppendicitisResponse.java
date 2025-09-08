package com.team1.soai.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AppendicitisResponse {
    private float appendcitis_probability;
    private Map<String, Float> concept_scores;
    private int num_views;
}

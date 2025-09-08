package com.team1.soai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class AppendicitisRequest {
    @JsonProperty("appendicitisUuidList")  // FastAPI가 기대하는 필드명
    private List<String> appendicitisUuidList;
}


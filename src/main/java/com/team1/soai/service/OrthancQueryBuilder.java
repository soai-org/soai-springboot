package com.team1.soai.service;

import com.team1.soai.dto.DicomTag;

import java.util.HashMap;
import java.util.Map;

public class OrthancQueryBuilder {
    private final Map<String, Object> queryMap = new HashMap<>();

    public OrthancQueryBuilder add(DicomTag tag, Object value) { // 타입 검증
        if (!tag.getValueType().isInstance(value)) {
            throw new IllegalArgumentException( "Invalid type for " + tag.name() + ": expected " + tag.getValueType().getSimpleName() );
        }

        queryMap.put(tag.name(), value);

        return this;
    }

    public Map<String, Object> build() {
        return queryMap;
    }
}
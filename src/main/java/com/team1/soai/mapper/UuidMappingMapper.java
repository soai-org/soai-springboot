package com.team1.soai.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UuidMappingMapper {

    /**
     * study_uuid로 해당 study의 latest_instance_uuid를 조회
     * @param studyUuid study UUID
     * @return latest instance UUID (없으면 null)
     */
    String getLatestInstanceUuidByStudyUuid(@Param("studyUuid") String studyUuid);
}
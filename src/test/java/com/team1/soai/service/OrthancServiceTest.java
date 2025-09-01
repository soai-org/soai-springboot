package com.team1.soai.service;

import com.team1.soai.dto.Level;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
public class OrthancServiceTest {

    private final OrthancService orthancService;

    @Autowired
    public OrthancServiceTest(OrthancService orthancService) {
        this.orthancService = orthancService;
    }

    @Test
    public void toolsFindSeriesByStudyUuidTest() throws Exception {
        // 테스트용 Study UUID (실제 존재하는 UUID여야 함)
        String studyUuid = "73ef034f-45c70abe-c208e522-c778212e-3cd9ca1a";

        List<?> result = orthancService.toolsFindSeriesByStudyUuid(studyUuid);


        log.info("Series list: {}", result);
    }

    @Test
    public void toolsFindTest() throws Exception {
        Map<String, Object> query = Map.of("PatientName", "장서성");
        List<String> result = orthancService.toolsFind(Level.Study.getValue(), query);

        log.info("Series list: {}", result);
    }

}

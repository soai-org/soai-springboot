package com.team1.soai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.soai.dto.Level;
import com.team1.soai.dto.StudyCardDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class DashBoardServiceTest {

    private final DashBoardService dashBoardService;

    @Autowired
    public DashBoardServiceTest(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @Test
    public void toolsFindTest() throws JsonProcessingException {
        String result = dashBoardService.toolsFind("장서성", Level.Study).toString();

        log.info("result: {}", result);
    }

    @Test
    public void toolsFindTestFail() throws JsonProcessingException {
        String result = dashBoardService.toolsFind("장거한", Level.Study).toString();

        log.info("result: {}", result);
    }

    @Test
    public void toolsFindExpandTest() throws JsonProcessingException {
        String result = dashBoardService.toolsFindExpand("장서성", Level.Study).toString();

        log.info("result: {}", result);
    }

    @Test
    public void toolsFindExpandTestFail() throws JsonProcessingException {
        String result = dashBoardService.toolsFindExpand("장거한", Level.Study).toString();

        log.info("result: {}", result);
    }

    @Test
    public void getStudyCardListTest() throws JsonProcessingException {
        int limit = 2;
        int since = 1;
        String PatientUuid = "b1d57811-11d84f7b-3fe45a08-52e59758-cd7a87e5";
        List<StudyCardDTO> studyCardDTOList = dashBoardService.getStudyCardList(limit,since, PatientUuid);

        ObjectMapper mapper = new ObjectMapper();
        String jsonOutput = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(studyCardDTOList);

        log.info("studyCardDTOList JSON:\n{}", jsonOutput);
    }

}

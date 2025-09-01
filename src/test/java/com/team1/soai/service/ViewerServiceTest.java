package com.team1.soai.service;

import com.team1.soai.dto.SeriesCardDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
public class ViewerServiceTest {


    private final ViewerService viewerService;

    @Autowired
    public ViewerServiceTest(ViewerService viewerService) {
        this.viewerService = viewerService;
    }

    @Test
    public void getSeriesListTest() throws Exception {

        String studyUuid = "73ef034f-45c70abe-c208e522-c778212e-3cd9ca1a";

        List<SeriesCardDTO> result = viewerService.getSeriesList(studyUuid);

        log.info("Series list: {}", result);
    }
}

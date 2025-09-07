package com.team1.soai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.soai.dto.SeriesCardDTO;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
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
    public void getDicomDataTest() throws Exception {
        String InstanceUuid = "d7d995ca-7dd1d862-c0482206-49acbe16-7d2c362d";

        byte[] dicomData = viewerService.getDicomData(InstanceUuid);

        // ByteArrayInputStream으로 DICOM 읽기
        try (DicomInputStream dis = new DicomInputStream(new ByteArrayInputStream(dicomData))) {
            Attributes attrs = dis.readDataset(-1, -1);

            // 예: PatientName, StudyDate, Modality 등 확인
            String patientName = attrs.getString(org.dcm4che3.data.Tag.PatientName, "Unknown");
            String studyDate = attrs.getString(org.dcm4che3.data.Tag.StudyDate, "Unknown");
            String modality = attrs.getString(org.dcm4che3.data.Tag.Modality, "Unknown");

            System.out.println("PatientName: " + patientName);
            System.out.println("StudyDate: " + studyDate);
            System.out.println("Modality: " + modality);
        }
    }

    @Test
    public void getSeriesListTest() throws Exception {

        String studyUuid = "73ef034f-45c70abe-c208e522-c778212e-3cd9ca1a";

        List<SeriesCardDTO> seriesCardDTOList = viewerService.getSeriesList(studyUuid);

        ObjectMapper mapper = new ObjectMapper();
        String jsonOutput = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(seriesCardDTOList);

        log.info("seriesCardDTOList JSON:\n{}", jsonOutput);
    }
}

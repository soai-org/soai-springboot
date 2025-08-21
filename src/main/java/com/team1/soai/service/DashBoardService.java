package com.team1.soai.service;

import com.team1.soai.dto.DicomTag;
import com.team1.soai.dto.Level;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashBoardService {
    private final OrthancService orthancService = new OrthancService();

    public String findLvPatientByName(String name) {
        OrthancQueryBuilder builder = new OrthancQueryBuilder();

        Map<String, Object> query = builder
                        .add(DicomTag.PatientName, name)
                        .build();

        return orthancService.toolsFind(Level.Patient.getValue(), query);
    }
}

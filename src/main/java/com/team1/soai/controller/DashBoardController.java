package com.team1.soai.controller;
import com.team1.soai.dto.Level;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.team1.soai.service.DashBoardService;

import java.util.Map;
import com.team1.soai.dto.PageResult;
import com.team1.soai.dto.StudyCardDTO;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashBoardController {

    private final DashBoardService dashBoardService;

    @PostMapping("/toolsfind")
    public ResponseEntity<?> toolsFind(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String levelStr = request.get("level");

        if (name == null || levelStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "name and level are required"));
        }

        Level level;
        try {
            level = Level.valueOf(levelStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid level: " + levelStr));
        }

        try {
            Object result = dashBoardService.toolsFind(name, level);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch data from Orthanc", "details", e.getMessage()));
        }
    }

    @PostMapping("/toolsfindfull")
    public ResponseEntity<?> toolsFindFull(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String levelStr = request.get("level");

        if (name == null || levelStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "name and level are required"));
        }

        Level level;
        try {
            level = Level.valueOf(levelStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid level: " + levelStr));
        }

        try {
            Object result = dashBoardService.toolsFindFull(name, level);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch data from Orthanc", "details", e.getMessage()));
        }
    }

    @PostMapping("/patient-studies")
    public ResponseEntity<?> getPatientStudies(@RequestBody Map<String, Object> request) {
        String patientId = (String) request.get("patientId");
        Integer page = (Integer) request.get("page");
        Integer size = (Integer) request.get("size");
        String sortBy = (String) request.get("sortBy");
        String sortOrder = (String) request.get("sortOrder");

        if (patientId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "patientId is required"));
        }

        // 기본값 설정
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sortBy == null) sortBy = "StudyDate";
        if (sortOrder == null) sortOrder = "DESC";

        try {
            PageResult<StudyCardDTO> result = dashBoardService.getPatientStudiesWithPagination(
                patientId, page, size, sortBy, sortOrder
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch patient studies", "details", e.getMessage()));
        }
    }



}

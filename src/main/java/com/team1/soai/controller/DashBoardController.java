package com.team1.soai.controller;
import com.team1.soai.dto.Level;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    /**
     * 이름으로 목록검색 API
     * @param request 이름
     * @return List<String> 환자 UUID목록 반환
     */
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

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/toolsfindexpand")
    public ResponseEntity<?> toolsFindExpand(@RequestBody Map<String, String> request) {
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
            Object result = dashBoardService.toolsFindExpand(name, level);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch data from Orthanc", "details", e.getMessage()));
        }
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/studycards")
    public ResponseEntity<?> getStudies(@RequestBody Map<String, String> request) {
        try {
            String patientUuid = request.get("patientUuid");
            String sizeStr = request.get("size");
            String pageStr = request.get("page");

            if (patientUuid == null || sizeStr == null || pageStr == null) {
                return ResponseEntity.badRequest().body("Missing required parameters: patientUuid, size, or page");
            }

            int size;
            int page;
            try {
                size = Integer.parseInt(sizeStr);
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid number format for size or page");
            }

            Object result = dashBoardService.studiesPagination(Level.Study.getValue(), patientUuid, size, page);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }


}

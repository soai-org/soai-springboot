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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
class DashBoardController {
    private final DashBoardService dashBoardService;

    // --------------------
    // 대시보드 환자(Patient) 검색
    // --------------------
    @PostMapping("/toolsfindbyname")
    public ResponseEntity<?> getByName(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String levelStr = request.get("level");  // "Patient", "Study", "Series", "Instance"

        if (name == null || levelStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "name and level are required"));
        }

        Level level;
        try {
            level = Level.valueOf(levelStr);  // 문자열 → enum 변환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid level: " + levelStr));
        }

        try {
            Object result = dashBoardService.findByName(name, level);
            return ResponseEntity.ok(result);  // 이미 List<DTO> 형태라 JSON 배열로 변환됨
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch data from Orthanc", "details", e.getMessage()));
        }
    }
}
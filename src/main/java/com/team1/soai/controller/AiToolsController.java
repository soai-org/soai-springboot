package com.team1.soai.controller;

import org.springframework.http.HttpStatus;
import com.team1.soai.service.CaptioningService;
import com.team1.soai.service.SegmentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.team1.soai.service.PatientService;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/x-ray")
public class AiToolsController {
    @Autowired
    private PatientService patientService;
    @Autowired
    private SegmentationService segmentationService;

    @PostMapping("/segmentation")
    public ResponseEntity<Map<String, Object>> segmentation(@RequestBody Map<String, String> request) {
        try {
            String instanceUuid = request.get("instanceUUID");
            String segmentationImage = segmentationService.getSegmentationImage(instanceUuid);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("instanceUUID",instanceUuid);
            response.put("segmentationImage", segmentationImage);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "세그멘테이션 이미지를 가져오는 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Autowired
    private CaptioningService captioningService;

    @PostMapping("/captioning")
    public ResponseEntity<Map<String, Object>> runCaptioning(@RequestBody Map<String, String> request) {
        try {
            String instanceUuid = request.get("instanceUUID");
            String description = request.get("description");

            if (instanceUuid == null || instanceUuid.isBlank()) {
                Map<String, Object> error = Map.of(
                        "status", "error",
                        "message", "studyId가 제공되지 않았습니다."
                );
                return ResponseEntity.badRequest().body(error);
            }

            String transcript = captioningService.getCaptioning(instanceUuid, description);

            Map<String, Object> response = Map.of(
                    "status", "success",
                    "instanceUUID", instanceUuid,
                    "transcript", transcript
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = Map.of(
                    "status", "error",
                    "message", "Captioning 처리 중 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
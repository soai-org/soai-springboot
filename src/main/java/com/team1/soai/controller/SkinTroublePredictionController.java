package com.team1.soai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.team1.soai.service.SkinTroublePredictionService;

import java.util.Map;
@RestController
@RequestMapping("/skin-trouble")
public class SkinTroublePredictionController {
    @Autowired
    private SkinTroublePredictionService skinTroublePredictionService;

    @PostMapping("/prediction")
    public ResponseEntity<Map<String,Object>> getPredictionForSkin(@RequestBody Map<String, String> request){
        try {
            String instanceUuid = request.get("instanceUUID");

            if (instanceUuid == null || instanceUuid.isBlank()) {
                Map<String, Object> error = Map.of(
                        "status", "error",
                        "message", "Instance UUID가 제공되지 않았습니다."
                );
                return ResponseEntity.badRequest().body(error);
            }
            Map<String, Object> prediction = skinTroublePredictionService.predictSkinTrouble(instanceUuid);
            return ResponseEntity.ok(prediction);
        }
        catch (Exception e){
            e.printStackTrace();
            Map<String, Object> error = Map.of(
                    "status", "error",
                    "message", "Skin Prediction 처리 중 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

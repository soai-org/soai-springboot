package com.team1.soai.controller;

import com.team1.soai.service.ViewerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/viewer")
public class ViewerController {

    @Autowired
    private final ViewerService viewerService;

    @PostMapping(
            value = "/dicomfile",
            produces = "application/dicom")
    public ResponseEntity<byte[]> getDicomData(@RequestBody Map<String, String> request){
        try {
            String instanceUuid = request.get("instanceUuid");
            return ResponseEntity.ok(viewerService.getDicomData(instanceUuid));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new byte[0]);
        }
    }

    @PostMapping("/serieslist")
    public ResponseEntity<?> getSeriesList(@RequestBody Map<String, String> request) {
        try{
            String studyUuid = request.get("studyUuid");
            Object result = viewerService.getSeriesList(studyUuid);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get series list: " + e.getMessage());
        }
    }
}

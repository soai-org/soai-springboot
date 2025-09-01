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
    public ResponseEntity<byte[]> getDicomData(@RequestParam String InstanceUuid){
        try {
            return ResponseEntity.ok(viewerService.getDicomData(InstanceUuid));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.getStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new byte[0]);
        }
    }

    @PostMapping("/serieslist")
    public ResponseEntity<?> getSeriesList(@RequestParam String studyUuid) {
        try{
            Object result = viewerService.getSeriesList(studyUuid);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get series list: " + e.getMessage());
        }
    }
}

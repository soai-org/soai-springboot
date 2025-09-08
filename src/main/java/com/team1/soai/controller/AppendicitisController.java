package com.team1.soai.controller;

import com.team1.soai.dto.AppendicitisRequest;
import com.team1.soai.dto.AppendicitisResponse;
import com.team1.soai.service.AppendicitisDiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appendicitis")
public class AppendicitisController {
    @Autowired
    private AppendicitisDiagnosisService appendicitisDiagnosisService;

    @PostMapping("/diagnosis")
    public  ResponseEntity<AppendicitisResponse> runAppendicitisDiagnosis(@RequestBody AppendicitisRequest request){
        AppendicitisResponse response = appendicitisDiagnosisService.getAppendicitisResponse(request);
        return ResponseEntity.ok(response);
    }
}

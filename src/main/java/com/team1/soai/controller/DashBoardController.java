package com.team1.soai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class DashBoardController {
    private final DashBoardService dashBoardService;

    // --------------------
    // 대시보드 환자 검색
    // --------------------
    @PostMapping("/toolsfindbyname")
    public String getPatient(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        log.info(dashBoardService.findLvPatientByName(name));
        return dashBoardService.findLvPatientByName(name);
    }
}

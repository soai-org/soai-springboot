package com.team1.soai.controller;
import com.team1.soai.dto.PatientDataResponse;
import com.team1.soai.service.CaptioningService;
import com.team1.soai.service.SegmentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.team1.soai.service.PatientService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class PatientsController {
    @Autowired
    private PatientService patientService;

    @Autowired
    private SegmentationService segmentationService;
    // 검색 폼 페이지
    @GetMapping("/dashboard")
    public String patientForm() {
        return "dashboard";
    }

    // 이름으로 환자 검색
    @PostMapping("/dashboard")
    public String searchPatient(@RequestParam("name") String patientName, Model model) {
        try {
            List<PatientDataResponse> patients = patientService.getPatientsByName(patientName);

            // 환자별 study → series → instance mapping
            List<Map<String, Object>> studyDataList = new ArrayList<>();
            for (PatientDataResponse patient : patients) {
                Map<String, List<String>> seriesMap = patientService.getSeriesByStudyIds(patient.getStudyIds());

                for (Map.Entry<String, List<String>> entry : seriesMap.entrySet()) {
                    String studyId = entry.getKey();
                    List<String> seriesIds = entry.getValue();

                    for (String seriesId : seriesIds) {
                        List<String> instanceIds = patientService.getInstancesBySeriesIds(List.of(seriesId)).get(seriesId);

                        List<String> previews = new ArrayList<>();
                        for (String instanceId : instanceIds) {
                            previews.add(patientService.getInstancePreviewUrl(instanceId));
                        }

                        Map<String, Object> map = new HashMap<>();
                        map.put("patient", patient);
                        map.put("studyId", studyId);
                        map.put("seriesId", seriesId);
                        map.put("images", previews);

                        studyDataList.add(map);
                    }
                }
            }

            model.addAttribute("studyData", studyDataList);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "환자 정보를 가져오는 중 오류가 발생했습니다.");
        }

        return "dashboard";
    }

    @PostMapping("/segmentation")
    public String aiToolPage(@RequestParam("studyId") String studyId,
                             @RequestParam("patientId") String patientId,
                             @RequestParam("seriesId") String seriesId,
                             @RequestParam("instanceId") String instanceId,
                             Model model) {
        String segmentationImage = segmentationService.getSegmentationImage(studyId);

        model.addAttribute("patientId", patientId);
        model.addAttribute("studyId", studyId);
        model.addAttribute("seriesId", seriesId);
        model.addAttribute("instanceId", instanceId);
        model.addAttribute("segmentationImage", segmentationImage);
        model.addAttribute("instancePreviewUrl", patientService.getInstancePreviewUrl(instanceId));

        return "segmentation";
    }
    @Autowired
    private CaptioningService captioningService;

    @PostMapping("/captioning")
    public String runCaptioning(@RequestParam("studyId") String studyId,
                                @RequestParam("patientId") String patientId,
                                @RequestParam("seriesId") String seriesId,
                                @RequestParam("instanceId") String instanceId, Model model){
        String transcript = captioningService.getCaptioning(studyId);
        String segmentationImage = segmentationService.getSegmentationImage(studyId);
        model.addAttribute("patientId", patientId);
        model.addAttribute("studyId", studyId);
        model.addAttribute("seriesId", seriesId);
        model.addAttribute("instanceId", instanceId);
        model.addAttribute("segmentationImage", segmentationImage);
        model.addAttribute("instancePreviewUrl", patientService.getInstancePreviewUrl(instanceId));
        model.addAttribute("transcript", transcript);
        return "captioning";
    }

}
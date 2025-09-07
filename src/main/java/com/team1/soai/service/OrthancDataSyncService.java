package com.team1.soai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrthancDataSyncService {
    
    // TODO: MyBatis Mapper 주입
    // private final PatientLatestDataMapper patientLatestDataMapper;
    
    /**
     * Orthanc 데이터 변경 이벤트 처리
     * 실제로는 Orthanc의 webhook이나 주기적인 동기화로 구현
     */
    public void handleOrthancDataChange(String patientId) {
        try {
            log.info("Orthanc data changed for patient: {}", patientId);
            updatePatientStudySummary(patientId);
        } catch (Exception e) {
            log.error("Failed to sync data for patient: {}", patientId, e);
        }
    }
    
    /**
     * Patient의 최신 Study/Series/Instance 정보를 테이블에 업데이트
     */
    private void updatePatientStudySummary(String patientId) {
        // TODO: 실제 구현
        // 1. Patient ID로 해당 Patient의 최신 Study UUID 조회
        // 2. 해당 Study의 최신 Series UUID 조회  
        // 3. 해당 Series의 최신 Instance UUID 조회
        // 4. PATIENT_LATEST_DATA 테이블 업데이트
    }
}

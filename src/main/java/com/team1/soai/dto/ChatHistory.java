package com.team1.soai.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ChatHistory {
    private Long id; // 대화 기록 고유 ID
    private String userMessage; // 사용자가 보낸 메시지
    private String botResponse; // 챗봇이 응답한 메시지 내용
    private Timestamp createdAt; // 메시지가 생성된 시간
    private String modelName; // 챗봇 모델이름 기록
}

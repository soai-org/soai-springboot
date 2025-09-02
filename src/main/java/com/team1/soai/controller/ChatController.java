package com.team1.soai.controller;

import com.team1.soai.dto.ChatRequest;
import com.team1.soai.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/llm")
public class ChatController {
    @Autowired
    private ChatService chatService;

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @PostMapping("/ask")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        ChatRequest chatRequest = new ChatRequest(message);
        try {
            String response = chatService.getChatResponse(chatRequest);        // 수정: 대화 기록을 저장하도록 추가
            chatService.saveChatHistory(message, response);
            return ResponseEntity.ok().body(Map.of("message", message,"response" ,response));
        }
        catch (Exception e) {
            log.error("Chat 처리 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Chat Response 과정에서 오류가 발생했습니다.");
        }
    }
}
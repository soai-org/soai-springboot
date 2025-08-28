package com.team1.soai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.team1.soai.dto.ChatRequest;
import com.team1.soai.dto.ChatHistory;
import com.team1.soai.mapper.ChatHistoryMapper;
import java.util.List;

@Service
public class ChatService {
    @Value("${fastapi.url:http://localhost:8000}") // FASTAPI url 값을 읽어오기
    private String fastApiUrl;

    private final RestTemplate restTemplate;
    private final ChatHistoryMapper chatHistoryMapper;

    @Autowired
    public ChatService(ChatHistoryMapper chatHistoryMapper) {
        this.restTemplate = new RestTemplate();
        this.chatHistoryMapper = chatHistoryMapper;
    }

    public String getChatResponse(ChatRequest chatRequest){
        String url = fastApiUrl + "/chat-bot/ask-llm";
        ChatResponse response = restTemplate.postForObject(url, chatRequest, ChatResponse.class);
        return response != null ? response.getResponse() : "";
    }
    @Transactional
    public void saveChatHistory(String userMessage, String botResponse) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setUserMessage(userMessage);
        chatHistory.setBotResponse(botResponse);
        chatHistory.setModelName("gemma3");
        chatHistoryMapper.insertChatHistory(chatHistory);
    }

    public List<ChatHistory> getChatHistory() {
        return chatHistoryMapper.selectChatHistory();
    }
}

class ChatResponse{
    private String response;

    public String getResponse(){
        return response;
    }

    public void setResponse(String response){
        this.response = response;
    }
}

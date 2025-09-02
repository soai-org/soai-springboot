package com.team1.soai.service;

import org.springframework.web.socket.WebSocketSession;

@FunctionalInterface
public interface WebSocketConnectCallback {
    void onConnect(WebSocketSession session);
}
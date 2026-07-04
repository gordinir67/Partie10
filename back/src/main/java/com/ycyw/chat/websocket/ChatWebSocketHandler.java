package com.ycyw.chat.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycyw.chat.dto.ChatMessageResponse;
import com.ycyw.chat.dto.CreateChatMessageRequest;
import com.ycyw.chat.service.ChatMessageService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler(ChatMessageService chatMessageService, ObjectMapper objectMapper) {
        this.chatMessageService = chatMessageService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        try {
            CreateChatMessageRequest request = objectMapper.readValue(
                    message.getPayload(),
                    CreateChatMessageRequest.class
            );

            ChatMessageResponse savedMessage = chatMessageService.createMessage(request);
            broadcast(savedMessage);
        } catch (IllegalArgumentException | JsonProcessingException exception) {
            sendError(session, exception.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessions.remove(session);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    private void broadcast(ChatMessageResponse message) throws IOException {
        String payload = objectMapper.writeValueAsString(message);
        TextMessage textMessage = new TextMessage(payload);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(textMessage);
            }
        }
    }

    private void sendError(WebSocketSession session, String detail) throws IOException {
        if (session.isOpen()) {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "error", detail == null || detail.isBlank() ? "Message WebSocket invalide." : detail
            ));
            session.sendMessage(new TextMessage(payload));
        }
    }
}

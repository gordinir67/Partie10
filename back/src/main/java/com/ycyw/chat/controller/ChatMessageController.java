package com.ycyw.chat.controller;

import com.ycyw.chat.dto.ChatMessageResponse;
import com.ycyw.chat.service.ChatMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat/messages")
public class ChatMessageController {

    private final ChatMessageService service;

    public ChatMessageController(ChatMessageService service) {
        this.service = service;
    }

    @GetMapping
    public List<ChatMessageResponse> findMessages(
            @RequestParam(required = false) Long afterId
    ) {
        return service.findMessages(afterId);
    }
}

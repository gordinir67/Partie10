package com.ycyw.chat.dto;

import java.time.Instant;

public record ChatMessageResponse(
        Long id,
        String author,
        String content,
        Instant createdAt
) {
}

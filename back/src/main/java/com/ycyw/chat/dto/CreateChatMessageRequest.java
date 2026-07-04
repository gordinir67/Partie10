package com.ycyw.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChatMessageRequest(
        @NotBlank(message = "Le pseudo est obligatoire.")
        @Size(max = 80, message = "Le pseudo ne doit pas dépasser 80 caractères.")
        String author,

        @NotBlank(message = "Le message est obligatoire.")
        @Size(max = 1000, message = "Le message ne doit pas dépasser 1000 caractères.")
        String content
) {
}

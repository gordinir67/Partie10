package com.ycyw.chat.service;

import com.ycyw.chat.dto.ChatMessageResponse;
import com.ycyw.chat.dto.CreateChatMessageRequest;
import com.ycyw.chat.model.ChatMessage;
import com.ycyw.chat.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository repository;

    public ChatMessageService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> findMessages(Long afterId) {
        if (afterId != null && afterId > 0) {
            return repository.findByIdGreaterThanOrderByIdAsc(afterId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return repository.findTop50ByOrderByIdDesc()
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getId))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ChatMessageResponse createMessage(CreateChatMessageRequest request) {
        String author = cleanAuthor(request.author());
        String content = cleanContent(request.content());

        ChatMessage message = new ChatMessage(author, content);
        ChatMessage savedMessage = repository.save(message);
        return toResponse(savedMessage);
    }

    private String cleanAuthor(String author) {
        if (author == null || author.trim().isBlank()) {
            throw new IllegalArgumentException("Le pseudo est obligatoire.");
        }

        String cleanedAuthor = author.trim();
        if (cleanedAuthor.length() > 80) {
            throw new IllegalArgumentException("Le pseudo ne doit pas dépasser 80 caractères.");
        }

        return cleanedAuthor;
    }

    private String cleanContent(String content) {
        if (content == null || content.trim().isBlank()) {
            throw new IllegalArgumentException("Le message est obligatoire.");
        }

        String cleanedContent = content.trim();
        if (cleanedContent.length() > 1000) {
            throw new IllegalArgumentException("Le message ne doit pas dépasser 1000 caractères.");
        }

        return cleanedContent;
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getAuthor(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}

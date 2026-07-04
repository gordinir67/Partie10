package com.ycyw.chat.repository;

import com.ycyw.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop50ByOrderByIdDesc();

    List<ChatMessage> findByIdGreaterThanOrderByIdAsc(Long id);
}

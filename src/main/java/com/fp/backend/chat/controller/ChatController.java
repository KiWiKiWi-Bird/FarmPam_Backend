package com.fp.backend.chat.controller;

import com.fp.backend.chat.domain.ChatMessage;
import com.fp.backend.chat.dto.*;
import com.fp.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/receive/{chatId}")
    @SendTo("/send/{chatId}")
    public MessageDTO sendMessage(MessageDTO dto) {
        return dto;
    }


    @PostMapping("/chats")
    public ResponseEntity<Long> createChat(@RequestBody NewChatInfoDTO dto) {
        log.info("{}", dto);
        Long chatId = chatService.createChat(dto);
        return new ResponseEntity<>(chatId, HttpStatus.OK);
    }

    @GetMapping("/chats/chatIds")
    public ResponseEntity<List<Long>> getChatIds(@RequestParam String userId) {
        List<Long> chatIds = chatService.getChatIds(userId);
        return new ResponseEntity<>(chatIds, HttpStatus.OK);
    }

    @PostMapping("/chats/chatPreviewInfos")
    public ResponseEntity<List<ChatPreviewInfoDTO>> getChatPreviewInfos(@RequestBody RequestChatPreviewInfoDTO dto) {
        return new ResponseEntity<>(chatService.getChatPreviewInfos(dto.getChatIds(), dto.getUserId()), HttpStatus.OK);
    }

    @GetMapping("/chats/chatDetailInfo")
    public ResponseEntity<ChatDetailInfoDTO> getChatDetailInfo(@RequestParam Long chatId,
                                                               @RequestParam String userId) {
        ChatDetailInfoDTO dto = chatService.getChatDetailInfo(chatId, userId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/chats/chatMessages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@RequestParam Long chatId) {
        List<ChatMessage> chatMessages = chatService.getChatMessages(chatId);
        return new ResponseEntity<>(chatMessages, HttpStatus.OK);
    }

    @PostMapping("/chats/chatMessage")
    public ResponseEntity<String> sendMessages(@RequestBody SendMessageDTO dto) {
        chatService.sendMessage(dto);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }
}

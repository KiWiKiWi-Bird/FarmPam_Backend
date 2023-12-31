package com.fp.backend.chat.service;

import com.fp.backend.account.entity.Users;
import com.fp.backend.account.repository.UserRepository;
import com.fp.backend.auction.entity.Item;
import com.fp.backend.auction.entity.ItemImg;
import com.fp.backend.auction.repository.ItemImgRepository;
import com.fp.backend.auction.repository.ItemRepository;
import com.fp.backend.chat.domain.Chat;
import com.fp.backend.chat.domain.ChatMessage;
import com.fp.backend.chat.dto.ChatDetailInfoDTO;
import com.fp.backend.chat.dto.ChatPreviewInfoDTO;
import com.fp.backend.chat.dto.NewChatInfoDTO;
import com.fp.backend.chat.dto.SendMessageDTO;
import com.fp.backend.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final UserRepository userRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Override
    public List<Long> getChatIds(String userId) {
        log.info("{}의 채팅 내역 검색", userId);
        return chatRepository.findChatsByFirstUserIdOrSecondUserId(userId)
                .stream().map(Chat::getChatId).collect(Collectors.toList());
    }

    @Override
    public List<ChatPreviewInfoDTO> getChatPreviewInfos(List<Long> chatIds, String userId) {

        log.info("채팅방 미리보기 정보 가져오기: getChatPreviewInfos");
        log.info("채팅 번호 {}", chatIds.toString());
        log.info("유저 아이디 {}", userId);

        List<ChatPreviewInfoDTO> result = new ArrayList<>();


        for (Long chatId : chatIds) {
            Chat chat = getChat(chatId);

            // 상대방 찾기
            String receiverId = getReceiverId(chat, userId);

            Users receiver = getUser(receiverId);

            // 경매 정보 찾기
            Item item = getItem(chat.getItemId());


            // 경매 썸네일 이미지 찾기
            ItemImg itemImg = itemImgRepository.findByItem(item)
                    .stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("경매 이미지가 존재하지 않습니다."));

            log.info("{}", itemImg.getImgUrl());


            // 마지막 메시지 가져오기
            List<ChatMessage> chatMessages = chat.getChatMessages();

            ChatMessage chatLastMessage = getLastChatMessage(chatMessages);

            result.add(ChatPreviewInfoDTO.builder()
                    .toNickName(receiver.getNickname())
                    .toNickNameThumbnailUrl(receiver.getImageUrl())
                    .lastMessage(chatLastMessage.getMessage())
                    .updateTime(chatLastMessage.getUpdatedAt())
                    .itemThumbnailUrl(itemImg.getImgUrl())
                    .build());
        }

        return result;
    }

    private Users getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자를 찾으려고 합니다."));
    }

    private ChatMessage getLastChatMessage(List<ChatMessage> chatMessages) {
        if (hasNotMessage(chatMessages)) {
            return ChatMessage.builder()
                    .message("")
                    .updatedAt("")
                    .build();
        }
        return chatMessages.get(chatMessages.size() - 1);
    }

    private boolean hasNotMessage(List<ChatMessage> chatMessages) {
        return chatMessages.isEmpty();
    }

    @Override
    public ChatDetailInfoDTO getChatDetailInfo(Long chatId, String userId) {

        log.info("채팅방 정보 가져오기: getChatDetailInfo");

        Chat chat = getChat(chatId);

        String receiverId = getReceiverId(chat, userId);

        Users receiver = getUser(receiverId);

        // 경매 정보 찾기
        Item item = getItem(chat.getItemId());

        ItemImg itemImg = itemImgRepository.findByItem(item)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("경매 이미지가 존재하지 않습니다."));

        return ChatDetailInfoDTO.builder()
                .toNickName(receiver.getNickname())
                .bidId(chat.getItemId())
                .itemTitle(item.getItemTitle())
                .itemThumbnailUrl(itemImg.getImgUrl())
                .biddingPrice((long) ((long) item.getCurrentBidPrice() == 0 ? item.getMinPrice() : item.getCurrentBidPrice()))
                .build();
    }

    @Override
    public List<ChatMessage> getChatMessages(Long chatId) {

        log.info("채팅 메시지 가져오기: getChatMessages");

        Chat chat = getChat(chatId);
        if (hasNotMessage(chat.getChatMessages())) {
            chat.addMessage(ChatMessage.builder()
                    .message("")
                    .updatedAt("")
                    .build());
        }
        return chat.getChatMessages();
    }

    @Override
    @Transactional
    public void sendMessage(SendMessageDTO dto) {

        log.info("채팅 메시지 보내기: sendMessage");

        log.info("{}", dto);

        Chat chat = getChat(dto.getChatId());
        chat.addMessage(dto.getChatMessage());

        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public Long createChat(NewChatInfoDTO dto) {

        Item item = getItem(dto.getItemId());

        validateUserNotSeller(dto, item);

        Chat oldChat = chatRepository.findByFirstUserIdOrSecondUserIdAndItemId(dto.getFirstUserId(), dto.getItemId());
        if (hasChat(oldChat)) {
            return oldChat.getChatId();
        }

        log.info("채팅방 생성: createChat");

        Long newChatId = sequenceGeneratorService.generateSequence(Chat.SEQUENCE_NAME);
        Chat chat = dtoToEntity(dto, newChatId);
        chatRepository.save(chat);
        return newChatId;
    }

    private void validateUserNotSeller(NewChatInfoDTO dto, Item item) {
        if (isSeller(dto.getFirstUserId(), item.getUsers().getUsername())) {
            throw new RuntimeException("자기 자신과 대화 할 수 없습니다.");
        }
    }

    private Item getItem(Long dto) {
        return itemRepository.findById(dto)
                .orElseThrow(() -> new RuntimeException("존재하지 않은 경매 물품을 찾으려고 합니다."));
    }

    private boolean isSeller(String userId, String username) {
        return Objects.equals(userId, username);
    }

    private boolean hasChat(Chat oldChat) {
        return !Objects.isNull(oldChat);
    }

    private Chat getChat(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("존재하지 않은 채팅을 찾을려고 합니다."));
    }

    private String getReceiverId(Chat chat, String userId) {
        return Objects.equals(chat.getFirstUserId(), userId) ? chat.getSecondUserId() : chat.getFirstUserId();
    }
}

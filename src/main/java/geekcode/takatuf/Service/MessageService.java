package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Chat;
import geekcode.takatuf.Entity.DeletedChat;
import geekcode.takatuf.Entity.Message;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Exception.Types.UnauthorizedException;
import geekcode.takatuf.Repository.ChatRepository;
import geekcode.takatuf.Repository.DeletedChatRepository;
import geekcode.takatuf.Repository.MessageRepository;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final DeletedChatRepository deletedChatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Message saveMessage(ChatMessage dto) {
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        // 👇 نحاول استرجاع الشات للمُرسل إذا كان محذوف
        restoreChatIfNeeded(sender, chat);

        // 👇 نحاول استرجاع الشات للمُستقبِل إذا كان محذوف
        restoreChatIfNeeded(receiver, chat);

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .receiver(receiver)
                .content(dto.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);

        // 🔔 إرسال إشعار للمستلم
        messagingTemplate.convertAndSendToUser(
                String.valueOf(receiver.getId()),
                "/queue/notifications",
                new ChatMessageNotification("new_message", savedMessage.getId())
        );

        return savedMessage;
    }


    private void restoreChatIfNeeded(User user, Chat chat) {
        deletedChatRepository.findByUserAndChat(user, chat)
                .ifPresent(deleted -> {
                    if (deleted.getRestoredAt() == null) {
                        deleted.setRestoredAt(LocalDateTime.now());
                        deletedChatRepository.save(deleted);
                    }
                });
    }

    @Transactional
    public Message editMessage(Long messageId, Long userId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getSender().getId().equals(userId)) {
            throw new UnauthorizedException("You are not the sender of this message");
        }

        message.setContent(newContent);
        message.setEdited(true);
        message.setEditedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getSender().getId().equals(userId)) {
            throw new UnauthorizedException("You are not allowed to delete this message");
        }

        message.setDeleted(true);
        message.setDeletedAt(LocalDateTime.now());
        messageRepository.save(message);


        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getReceiver().getId(),
                new ChatMessageNotification("deleted", messageId)
        );
    }


    public List<MessagesResponse> getMessages(Long userId, Long chatId, LocalDateTime before) {
        LocalDateTime endDate = (before != null) ? before : LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Message> messages = messageRepository.findByChatIdAndTimestampBetweenAndDeletedFalseOrderByTimestampAsc(
                chatId, startDate, endDate
        );

        var deletedOpt = deletedChatRepository.findByUserAndChat(user, chat);
        if (deletedOpt.isPresent()) {
            var deleted = deletedOpt.get();
            if (deleted.getRestoredAt() != null) {
                // فلترة الرسائل يلي قبل الاستعادة
                messages = messages.stream()
                        .filter(msg -> msg.getTimestamp().isAfter(deleted.getRestoredAt()))
                        .toList();
            } else {
                // حذف بدون استعادة
                return List.of();
            }
        }

        return messages.stream()
                .map(MessagesResponse::fromEntity)
                .toList();
    }


    public List<MessagesResponse> getMessagesForChat(Long chatId, Long userId, int monthsAgo) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isDeleted = deletedChatRepository
                .existsByUserAndChat(user, chat);

        if (isDeleted) {
            return List.of();
        }


        LocalDateTime from = LocalDateTime.now().minusMonths(monthsAgo);

        List<Message> messages = messageRepository
                .findByChatIdAndTimestampAfterOrderByTimestampAsc(chatId, from);

        return messages.stream()
                .filter(message -> !message.isDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    private MessagesResponse mapToResponse(Message message) {
        return MessagesResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .senderName(message.getSender() != null ? message.getSender().getName() : null)
                .receiverName(message.getReceiver() != null ? message.getReceiver().getName() : null)
                .build();
    }


    public List<ChatSummaryResponse> getUserChats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Chat> chats = chatRepository.findAllByUserId(userId);

        List<ChatSummaryResponse> summaries = new ArrayList<>();

        for (Chat chat : chats) {
            Optional<DeletedChat> deletedChatOpt = deletedChatRepository.findByUserAndChat(user, chat);
            LocalDateTime deletedAt = deletedChatOpt.map(DeletedChat::getDeletedAt).orElse(null);

            Optional<Message> lastMessageOpt = messageRepository.findTopByChatOrderByTimestampDesc(chat);

            if (lastMessageOpt.isEmpty()) continue;

            Message lastMessage = lastMessageOpt.get();
            if (deletedAt != null && lastMessage.getTimestamp().isBefore(deletedAt)) continue;

            User otherUser = chat.getUser1().getId().equals(userId) ? chat.getUser2() : chat.getUser1();

            summaries.add(new ChatSummaryResponse(
                    chat.getId(),
                    otherUser.getId(),
                    otherUser.getName(),
                    otherUser.getProfileImageUrl(),
                    lastMessage.getContent(),
                    lastMessage.getTimestamp()
            ));
        }

        return summaries.stream()
                .sorted(Comparator.comparing(ChatSummaryResponse::lastMessageTime).reversed())
                .toList();
    }

}

package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Chat;
import geekcode.takatuf.Entity.Message;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Exception.Types.UnauthorizedException;
import geekcode.takatuf.Repository.ChatRepository;
import geekcode.takatuf.Repository.DeletedChatRepository;
import geekcode.takatuf.Repository.MessageRepository;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.dto.ChatMessage;
import geekcode.takatuf.dto.ChatMessageNotification;
import geekcode.takatuf.dto.MessagesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import geekcode.takatuf.dto.MessageResponse;
import java.time.LocalDateTime;
import java.util.List;

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

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .receiver(receiver)
                .content(dto.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        deletedChatRepository.findByUserAndChat( sender, chat)
                .ifPresent(deleted -> deletedChatRepository.delete(deleted));

        deletedChatRepository.findByUserAndChat( receiver,chat)
                .ifPresent(deleted -> deletedChatRepository.delete(deleted));

        return messageRepository.save(message);
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

        // إرسال إشعار للـ frontend عبر WebSocket
        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getReceiver().getId(), // أو chatId حسب اللوجيك
                new ChatMessageNotification("deleted", messageId)
        );
    }



    public List<MessagesResponse> getMessages(Long chatId, LocalDateTime before) {
        LocalDateTime endDate = (before != null) ? before : LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);

        List<Message> messages = messageRepository.findByChatIdAndTimestampBetweenAndDeletedFalseOrderByTimestampDesc(
                chatId, startDate, endDate
        );

        return messages.stream()
                .map(MessagesResponse::fromEntity)
                .toList();
    }

    public List<MessagesResponse> getMessagesForChat(Long chatId, Long userId, int monthsAgo) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // نتحقق إذا المستخدم حذف الشات
        boolean isDeleted = deletedChatRepository
                .existsByUserAndChat(user, chat);

        if (isDeleted) {
            return List.of(); // المستخدم حذف الشات، لا نعرض له الرسائل
        }

        // نحسب تاريخ أول يوم في الشهر المطلوب
        LocalDateTime from = LocalDateTime.now().minusMonths(monthsAgo);

        List<Message> messages = messageRepository
                .findByChatIdAndTimestampAfterOrderByTimestampAsc(chatId, from);

        return messages.stream()
                .filter(message -> !message.isDeleted()) // لا نعرض الرسائل المحذوفة
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

}

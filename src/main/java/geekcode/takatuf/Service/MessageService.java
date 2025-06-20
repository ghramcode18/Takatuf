package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Chat;
import geekcode.takatuf.Entity.Message;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Repository.ChatRepository;
import geekcode.takatuf.Repository.MessageRepository;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

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

        return messageRepository.save(message);
    }
}

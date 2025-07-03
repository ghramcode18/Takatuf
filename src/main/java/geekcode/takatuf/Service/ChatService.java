package geekcode.takatuf.Service;


import geekcode.takatuf.Entity.Chat;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.ChatRepository;
import geekcode.takatuf.Repository.MessageRepository;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public Chat createChat(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new ResourceNotFoundException("User1 not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new ResourceNotFoundException("User2 not found"));

        // تحقق من وجود الشات مسبقاً
        Optional<Chat> existingChat = chatRepository.findByUser1AndUser2(user1, user2)
                .or(() -> chatRepository.findByUser2AndUser1(user1, user2));

        if (existingChat.isPresent()) {
            return existingChat.get(); // موجود مسبقاً
        }

        Chat chat = Chat.builder()
                .user1(user1)
                .user2(user2)
                .createdAt(LocalDateTime.now())
                .build();

        return chatRepository.save(chat);
    }

    public ChatResponse mapToResponse(Chat chat) {
        return ChatResponse.builder()
                .chatId(chat.getId())
                .user1Id(chat.getUser1().getId())
                .user2Id(chat.getUser2().getId())
                .createdAt(chat.getCreatedAt())
                .build();
    }

}

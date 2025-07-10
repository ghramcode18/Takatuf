package geekcode.takatuf.Controller;


import geekcode.takatuf.Entity.Chat;
import geekcode.takatuf.Entity.DeletedChat;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.ChatRepository;
import geekcode.takatuf.Repository.DeletedChatRepository;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Service.ChatService;
import geekcode.takatuf.Service.MessageService;
import geekcode.takatuf.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    private final UserRepository userRepository;
    private final MessageService messageService;
    private final ChatRepository chatRepository;
    private final DeletedChatRepository deletedChatRepository;

    @PostMapping("/create")
    public ResponseEntity<ChatResponse> createChat(@RequestBody ChatCreateRequest request,
    @AuthenticationPrincipal UserDetails userDetails) {
        Chat chat = chatService.createChat(request.getUser1Id(), request.getUser2Id());
        return ResponseEntity.ok(chatService.mapToResponse(chat));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessagesResponse>> getMessages(
            @PathVariable Long chatId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(messageService.getMessages(userId,chatId, before));
    }


//    @GetMapping("/{chatId}/visible/messages")
//    public ResponseEntity<List<MessagesResponse>> getMessagesForChat(
//            @PathVariable Long chatId,
//            @RequestParam int monthsAgo,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        Long userId = extractUserId(userDetails);
//        return ResponseEntity.ok(messageService.getMessagesForChat(chatId,userId, monthsAgo));
//    }


    @GetMapping("/user/{userId}/chats")
    public ResponseEntity<List<ChatSummaryResponse>> getUserChats(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getUserChats(userId));
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<String> deleteChat(@PathVariable Long chatId, @RequestParam Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        deletedChatRepository.findByUserAndChat(user, chat)
                .ifPresentOrElse(
                        deleted -> {
                            deleted.setDeletedAt(LocalDateTime.now());
                            deleted.setRestoredAt(null);
                            deletedChatRepository.save(deleted);
                        },
                        () -> {
                            DeletedChat deletedChat = DeletedChat.builder()
                                    .user(user)
                                    .chat(chat)
                                    .deletedAt(LocalDateTime.now())
                                    .restoredAt(null)
                                    .build();
                            deletedChatRepository.save(deletedChat);
                        }
                );

        return ResponseEntity.ok("Chat deleted successfully.");
    }
    private Long extractUserId(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}

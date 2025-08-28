package geekcode.takatuf.Controller;


import geekcode.takatuf.Entity.Message;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Service.MessageService;
import geekcode.takatuf.dto.chat.EditMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import geekcode.takatuf.dto.MessageResponse;
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final MessageService messageService;

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editMessage(@PathVariable Long id,
                                         @RequestBody EditMessageRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Message updatedMessage = messageService.editMessage(id, user.getId(), request.getContent());

        // نرسل التحديث عبر WebSocket
        messagingTemplate.convertAndSend(
                "/topic/chat/" + updatedMessage.getReceiver().getId(), // أو chatId لو عندك
                updatedMessage
        );

        return ResponseEntity.ok(updatedMessage);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        messageService.deleteMessage(id, user.getId());

        return ResponseEntity.ok(new MessageResponse("Message deleted successfully"));
    }

}

package geekcode.takatuf.Controller;


import geekcode.takatuf.Entity.Chat;
import geekcode.takatuf.Service.ChatService;
import geekcode.takatuf.Service.MessageService;
import geekcode.takatuf.dto.ChatCreateRequest;
import geekcode.takatuf.dto.ChatResponse;
import geekcode.takatuf.dto.MessagesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import geekcode.takatuf.dto.MessageResponse;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    private final MessageService messageService;

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

        return ResponseEntity.ok(messageService.getMessages(chatId, before));
    }


}

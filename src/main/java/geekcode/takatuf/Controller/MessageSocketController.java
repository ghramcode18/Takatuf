package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.MessageService;
import geekcode.takatuf.dto.chat.ChatMessage;
import geekcode.takatuf.Entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.send") // /app/chat.send
    public void sendMessage(ChatMessage chatMessage) {
        Message saved = messageService.saveMessage(chatMessage);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatMessage.getChatId(),
                saved
        );
    }
}

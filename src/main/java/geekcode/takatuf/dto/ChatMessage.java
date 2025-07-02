package geekcode.takatuf.dto;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long chatId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime timestamp;
}

package geekcode.takatuf.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageNotification {
    private String type; // مثل "deleted", "edited"
    private Long messageId;
}

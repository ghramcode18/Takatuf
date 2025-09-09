package geekcode.takatuf.dto;


import geekcode.takatuf.Entity.Message;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@Setter
@Getter
public class MessagesResponse {
    private Long id;
    private String content;
    private boolean deleted;
    private String senderName;
    private String receiverName;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime timestamp;

    public static MessagesResponse fromEntity(Message message) {
        return MessagesResponse.builder()
                .id(message.getId())
                .content(message.isDeleted() ? "❌ تم حذف هذه الرسالة" : message.getContent())
                .deleted(message.isDeleted())
                .senderName(message.getSender().getName())
                .receiverName(message.getReceiver().getName())
                .receiverId(message.getReceiver().getId())
                .senderId(message.getSender().getId())
                .timestamp(message.getTimestamp())
                .build();
    }
}

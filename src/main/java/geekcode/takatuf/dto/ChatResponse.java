package geekcode.takatuf.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatResponse {
    private Long chatId;
    private Long user1Id;
    private Long user2Id;
    private LocalDateTime createdAt;
}

package geekcode.takatuf.dto.chat;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatCreateRequest {
    private Long user1Id;
    private Long user2Id;
}

package geekcode.takatuf.dto;
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

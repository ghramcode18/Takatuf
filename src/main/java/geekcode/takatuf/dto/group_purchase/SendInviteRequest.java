package geekcode.takatuf.dto.group_purchase;

import lombok.Data;

@Data
public class SendInviteRequest {
    private Long senderId;
    private Long receiverId;
    private String message;
    private Long productId;
}

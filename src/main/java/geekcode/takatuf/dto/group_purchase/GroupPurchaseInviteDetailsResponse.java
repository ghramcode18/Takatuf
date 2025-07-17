package geekcode.takatuf.dto.group_purchase;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupPurchaseInviteDetailsResponse {
    private Long inviteId;

    private Long productId;
    private String productName;
    private String productPhoto;
    private Double oldPrice;
    private Double newPrice;

    private String senderName;
    private String senderPhoto;

    private String receiverName;
    private String receiverPhoto;
}

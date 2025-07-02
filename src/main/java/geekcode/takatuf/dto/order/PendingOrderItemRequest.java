package geekcode.takatuf.dto.order;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class PendingOrderItemRequest {
    private Long productId;
    private int quantity;
}
package geekcode.takatuf.dto.order;


import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class PendingOrderItemResponse {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
}

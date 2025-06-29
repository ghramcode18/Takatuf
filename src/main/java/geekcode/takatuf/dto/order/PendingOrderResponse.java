package geekcode.takatuf.dto.order;


import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class PendingOrderResponse {
    private Long orderId;
    private BigDecimal total;
    private List<PendingOrderItemResponse> items;
}
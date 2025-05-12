package geekcode.takatuf.dto.order;

import lombok.*;
import geekcode.takatuf.Enums.*;
import java.math.BigDecimal;
import java.time.LocalDateTime; 

@Data
@Builder
public class CustomOrderResponse {
    private Long customOrderId;
    private String category;
    private String customizationDetails;
    private BigDecimal proposedPrice;
    private OrderStatus status;
    private TrackingInfo trackingInfo;
    private LocalDateTime createdAt;
}

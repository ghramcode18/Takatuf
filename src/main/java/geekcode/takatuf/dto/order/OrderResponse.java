package geekcode.takatuf.dto.order;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import geekcode.takatuf.Enums.OrderStatus;
import geekcode.takatuf.Enums.OrderType;
import geekcode.takatuf.Enums.TrackingInfo;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private OrderStatus status;
    private TrackingInfo trackingInfo;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private OrderType orderType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;

    // Fields for custom orders (nullable)
    private String category;
    private String customizationDetails;
    private BigDecimal proposedPrice;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }
}

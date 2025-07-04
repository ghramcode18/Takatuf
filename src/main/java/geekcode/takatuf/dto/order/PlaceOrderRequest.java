package geekcode.takatuf.dto.order;

import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import geekcode.takatuf.Enums.OrderType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderRequest {

    private List<@Valid OrderItemRequest> items;

    @NotNull(message = "Order type is required")
    private OrderType orderType;

    private String address;

    // IF ORDER TYPE IS CUSTOM
    private Long categoryId;

    private String customizationDetails;

    private String imageUrl;
    private BigDecimal buyerProposedPrice;

    @Positive(message = "Proposed price must be greater than zero")
    private BigDecimal proposedPrice;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull
        @Positive(message = "Quantity must be greater than zero")
        private Integer quantity;
    }
}

package geekcode.takatuf.dto.order;

import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

import geekcode.takatuf.Enums.OrderType;

@Valid
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderRequest {
    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotEmpty(message = "Order items cannot be empty")
    private List<@Valid OrderItemRequest> items;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull
    private OrderType orderType;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        private Integer quantity;
    }
}

package geekcode.takatuf.dto.order;

import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

import geekcode.takatuf.Enums.OrderType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class PlaceOrderRequest {

    private List<@Valid OrderItemRequest> items;

    @NotNull
    private OrderType orderType;

    // Fields for custom orders (only if orderType == CUSTOM)
    private String category;
    private String customizationDetails;

<<<<<<< HEAD
=======
    private String address;

>>>>>>> b2a8628fee48e2cf35c8a0849a1c200336ddb60c
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        private Long productId;

        @Positive(message = "Quantity must be greater than zero")
        private Integer quantity;
    }
}

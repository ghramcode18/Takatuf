package geekcode.takatuf.dto.order;

import geekcode.takatuf.Enums.OrderType;
import geekcode.takatuf.Enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class PendingOrderReviewResponse {
    private Long pendingOrderId;
    private String recipientName;
    private String region;
    private String streetName;
    private String buildingNumber;
    private String phoneNumber;
    private PaymentMethod paymentMethod;
    private BigDecimal totalPrice;
    private OrderType orderType;
    private List<Item> items;

    @Builder
    @Data
    public static class Item {
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal price;
<<<<<<< HEAD
        private String image;
=======
>>>>>>> b2a8628fee48e2cf35c8a0849a1c200336ddb60c
    }
}

package geekcode.takatuf.dto;

import lombok.Data;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PromotionResponse {
    private Long id;

    private Long productId;
    private String productName;
    private String productImage;
    private Long storeId;
    private String storeName;

    private Long sellerId;
    private String sellerName;


    private String status;           // APPROVED, REJECTED, PENDING
    private Integer durationDays;
    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}

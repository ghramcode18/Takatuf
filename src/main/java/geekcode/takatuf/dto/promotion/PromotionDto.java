package geekcode.takatuf.dto.promotion;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import geekcode.takatuf.Enums.PromotionTargetType;

@Data

public class PromotionDto {

    @Getter
    @Setter
    public static class PromotionRequest {
        private String title;
        private String description;
        private BigDecimal discountPercentage;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Boolean active;
        private Long storeId;
        private PromotionTargetType targetType;
    }

    @Getter
    @Setter
    @Builder
    public static class PromotionResponse {
        private Long id;
        private String title;
        private String description;
        private BigDecimal discountPercentage;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Boolean active;
        private Long storeId;
        private String storeName;
        private PromotionTargetType targetType;
    }
}
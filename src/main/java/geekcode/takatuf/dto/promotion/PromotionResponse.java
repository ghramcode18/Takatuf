package geekcode.takatuf.dto.promotion;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import geekcode.takatuf.Enums.PromotionTargetType;

@Builder
@Getter
@Setter
public class PromotionResponse {
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
    private String imageUrl;
}

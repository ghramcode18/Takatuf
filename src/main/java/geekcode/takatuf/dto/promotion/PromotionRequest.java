package geekcode.takatuf.dto.promotion;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import geekcode.takatuf.Enums.*;

import org.springframework.format.annotation.DateTimeFormat;

@Data
public class PromotionRequest {
    private String title;
    private String description;
    private Double discountPercentage;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private Boolean active;
    private Long storeId;
    private PromotionTargetType targetType;

    private MultipartFile image;
}

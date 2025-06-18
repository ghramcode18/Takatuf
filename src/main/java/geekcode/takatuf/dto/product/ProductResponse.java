package geekcode.takatuf.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long storeId;
    private String storeName;
    private String storeImage;
    private String sellerName;
    private String sellerImage;
    private Double averageRating;
}

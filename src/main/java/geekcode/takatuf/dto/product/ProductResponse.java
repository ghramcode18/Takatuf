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
    private BigDecimal groupDiscountPercentage;
    private String image;
    private Long categoryId;
    private String categoryName;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long storeId;
    private String storeName;
    private String storeImage;
    private Long sellerId;
    private String sellerName;
    private String sellerImage;
    private Double averageRating;
    private Boolean favorited; 
}

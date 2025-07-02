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
<<<<<<< HEAD
=======
    private Integer quantity;
>>>>>>> b2a8628fee48e2cf35c8a0849a1c200336ddb60c
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long storeId;
    private String storeName;
    private String storeImage;
<<<<<<< HEAD
=======
    private Long sellerId;
>>>>>>> b2a8628fee48e2cf35c8a0849a1c200336ddb60c
    private String sellerName;
    private String sellerImage;
    private Double averageRating;
}

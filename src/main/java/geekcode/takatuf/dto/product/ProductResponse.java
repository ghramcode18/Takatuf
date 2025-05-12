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
    private Integer piece;
    private BigDecimal price;
    private String image;
    private String category;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long storeId;
    private String storeName;
}

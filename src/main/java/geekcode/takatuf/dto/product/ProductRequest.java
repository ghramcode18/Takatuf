package geekcode.takatuf.dto.product;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Integer piece;
    private BigDecimal price;
    private String image;
    private String category;
    private String color;
}

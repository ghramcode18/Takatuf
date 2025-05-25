package geekcode.takatuf.dto.product;

import lombok.Data;
import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private MultipartFile image;
    private String category;
}

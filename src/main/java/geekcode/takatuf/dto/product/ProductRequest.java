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
<<<<<<< HEAD
=======
    private Integer quantity ;
>>>>>>> b2a8628fee48e2cf35c8a0849a1c200336ddb60c
}

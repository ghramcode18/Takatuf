package geekcode.takatuf.dto.review;

import lombok.Data;

@Data
public class ProductReviewRequest {
    private Long productId;
    private Integer rating;
    private String comment;
}

package geekcode.takatuf.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductReviewRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Rating is required")
    private Integer rating;

    @NotBlank(message = "Comment is required")
    private String comment;
}

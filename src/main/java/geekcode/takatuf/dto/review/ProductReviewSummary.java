package geekcode.takatuf.dto.review;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProductReviewSummary {
    private Double averageRating;
    private Integer totalReviews;
    private List<ProductReviewResponse> ratedReviews;
}

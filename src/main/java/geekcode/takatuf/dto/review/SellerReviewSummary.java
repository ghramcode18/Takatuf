package geekcode.takatuf.dto.review;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SellerReviewSummary {
    private Double averageRating;
    private Integer totalReviews;
    private List<SellerReviewResponse> ratedReviews;
}

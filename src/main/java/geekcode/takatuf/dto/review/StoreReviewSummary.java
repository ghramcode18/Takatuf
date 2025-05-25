package geekcode.takatuf.dto.review;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StoreReviewSummary {
    private double averageRating;
    private int totalReviews;
    private List<StoreReviewResponse> ratedReviews;
}
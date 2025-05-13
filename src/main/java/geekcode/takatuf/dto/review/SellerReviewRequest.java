package geekcode.takatuf.dto.review;

import lombok.Data;

@Data
public class SellerReviewRequest {
    private Long sellerId;
    private Integer rating;
    private String comment;
}

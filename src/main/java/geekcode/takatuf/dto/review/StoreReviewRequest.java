package geekcode.takatuf.dto.review;

import lombok.Data;

@Data
public class StoreReviewRequest {
    private Long storeId;
    private Integer rating;
    private String comment;
}
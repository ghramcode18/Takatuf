package geekcode.takatuf.dto.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StoreReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private String reviewerName;
    private String reviewerImage;
    private LocalDateTime createdAt;
}
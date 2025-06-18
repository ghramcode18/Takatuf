package geekcode.takatuf.dto.review;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private String userName;
    private String userImage;
    private LocalDateTime createdAt;
}
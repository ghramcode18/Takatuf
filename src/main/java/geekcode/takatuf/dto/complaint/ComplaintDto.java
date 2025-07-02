package geekcode.takatuf.dto.complaint;

import geekcode.takatuf.Enums.ComplaintStatus;
import lombok.*;

import java.time.LocalDateTime;

public class ComplaintDto {

    @Getter
    @Setter
    public static class ComplaintRequest {
        private String subject;
        private String details;
        private Long orderId;
    }

    @Getter
    @Setter
    @Builder
    public static class ComplaintResponse {
        private Long id;
        private String subject;
        private String details;
        private ComplaintStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime reviewedAt;
        private String decision;
        private String submittedBy;
        private String reviewedBy;
        private Long orderId;
    }

    @Getter
    @Setter
    public static class ComplaintReviewRequest {
        private ComplaintStatus status;
        private String decision;
    }
}

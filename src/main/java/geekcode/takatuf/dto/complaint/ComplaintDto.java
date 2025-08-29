package geekcode.takatuf.dto.complaint;

import geekcode.takatuf.Enums.ComplaintStatus;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ComplaintDto {

    @Getter
    @Setter
    public static class ComplaintRequest {
    @NotBlank(message = "Complaint subject is required")
    @Size(min = 3, max = 120, message = "Subject must be 3–120 characters")
    private String subject;

    @Size(max = 2000, message = "Details must be at most 2000 characters")
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

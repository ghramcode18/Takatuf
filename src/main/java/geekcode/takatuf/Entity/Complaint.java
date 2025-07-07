package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import geekcode.takatuf.Enums.*;

@Entity
@Table(name = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;

    private String subject;
    private String details;

    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;
    private String decision;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User submittedBy;

    @ManyToOne
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;
}

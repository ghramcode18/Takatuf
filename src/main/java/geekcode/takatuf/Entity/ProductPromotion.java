package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import geekcode.takatuf.Enums.*;

@Entity
@Table(name = "product_promotions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User seller;

    @ManyToOne(optional = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private PromotionStatus status; // PENDING / APPROVED / REJECTED

    private Integer durationDays;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private LocalDateTime createdAt;

    @ManyToOne
    private User decidedBy; // Admin
    private LocalDateTime decidedAt;
}

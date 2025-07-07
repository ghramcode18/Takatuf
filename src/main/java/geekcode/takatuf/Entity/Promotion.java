package geekcode.takatuf.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import geekcode.takatuf.Enums.*;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private BigDecimal discountPercentage;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean active;

    @Enumerated(EnumType.STRING)
    private PromotionTargetType targetType;

    @Column(name = "store_id")
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private Store store;
}

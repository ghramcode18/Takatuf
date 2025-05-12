package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import geekcode.takatuf.Enums.OrderStatus;
import geekcode.takatuf.Enums.TrackingInfo;

@Entity
@Table(name = "custom_orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Store store;

    private String category;

    private String customizationDetails;

    private BigDecimal proposedPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;
    @Column(name = "tracking_info", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TrackingInfo trackingInfo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_order_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomOrderOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private User seller;

    private String additionalInfo;

    private java.math.BigDecimal proposedPrice;

    @Enumerated(EnumType.STRING)
    private geekcode.takatuf.Enums.OfferStatus status;

    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}

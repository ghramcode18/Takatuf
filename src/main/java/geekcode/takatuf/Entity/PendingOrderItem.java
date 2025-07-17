package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PendingOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PendingOrder pendingOrder;

    @ManyToOne
    private Product product;

    private int quantity;

    private BigDecimal price;

    private LocalDateTime addedAt;

    private String image;
}

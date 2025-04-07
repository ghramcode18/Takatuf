package geekcode.takatuf.Entity;

import java.math.BigDecimal;
import java.sql.Date;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.UUID;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;
    private String status;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private String trackingInfo;
    private String orderType;
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
}

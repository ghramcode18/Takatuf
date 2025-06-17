package geekcode.takatuf.Entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

import geekcode.takatuf.Enums.OrderStatus;
import geekcode.takatuf.Enums.OrderType;
import geekcode.takatuf.Enums.TrackingInfo;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalPrice;
    private BigDecimal proposedPrice;

    private String paymentMethod;

    private String category;
    private String customizationDetails;

    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "tracking_info")
    private TrackingInfo trackingInfo;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    @JsonIgnore
    private Store store;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrderItem> orderItems;
}

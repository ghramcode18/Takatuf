package geekcode.takatuf.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import geekcode.takatuf.Enums.*;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "name")
    private String name;
    private String customizationDetails;


    private String firstname;
    private String lastname;
    private String region;
    private String streetName;
    private String buildingNumber;
    private String phoneNumber;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private BigDecimal buyerProposedPrice;

    @Column(name = "created_at")
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
    @JoinColumn(name = "saller_id")
    @JsonIgnore
    private User saller;

    @ManyToOne
    @JoinColumn(name = "store_id")
    @JsonIgnore
    private Store store;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrderItem> orderItems;
}

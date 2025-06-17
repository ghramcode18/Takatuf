package geekcode.takatuf.Entity;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import geekcode.takatuf.Enums.OrderStatus;
import geekcode.takatuf.Enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private User user;

    private String recipientName;

    private String region;
    private String streetName;
    private String buildingNumber;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

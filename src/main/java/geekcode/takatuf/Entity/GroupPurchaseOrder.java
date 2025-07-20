package geekcode.takatuf.Entity;

import geekcode.takatuf.Enums.GroupPurchaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "group_purchase_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupPurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;


    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToMany
    @JoinTable(
            name = "group_purchase_participants",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private GroupPurchaseStatus status;


    @OneToOne
    @JoinColumn(name = "invitation_id")
    private GroupPurchaseInvite invitation;

}

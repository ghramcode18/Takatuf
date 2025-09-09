package geekcode.takatuf.Entity;
import geekcode.takatuf.Enums.InviteStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_purchase_invites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupPurchaseInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "message")
    private String message;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InviteStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "product_id")
    private Long productId;

}

package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
    private String type;

    @Column(name = "created_at")
    private Timestamp createdAt;

    // One-to-One with Profile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;

    // One-to-Many with Orders (as buyer)
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    // One-to-Many with Payments
    @OneToMany(mappedBy = "user")
    private List<Payment> payments;

    // One-to-Many with Messages (sent and received)
    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver")
    private List<Message> receivedMessages;

    // One-to-Many with Reviews
    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    // One-to-Many with Complaints
    @OneToMany(mappedBy = "user")
    private List<Complaint> complaints;

    // One-to-Many with Notifications
    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    // One-to-Many with Subscriptions
    @OneToMany(mappedBy = "user")
    private List<Subscription> subscriptions;

    // One-to-Many with BannedUsers
    @OneToMany(mappedBy = "user")
    private List<BannedUser> bannedUsers;
}

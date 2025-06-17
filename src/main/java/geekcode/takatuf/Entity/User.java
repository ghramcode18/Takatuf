package geekcode.takatuf.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import geekcode.takatuf.Enums.UserType;

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

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING) // Store as enum value
    private UserType type;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-One with Profile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Profile profile;

    // One-to-Many with user_roles
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserRole> userRoles;

    @OneToMany(mappedBy = "user1")
    @JsonIgnore
    private List<Chat> chatsInitiated;

    @OneToMany(mappedBy = "user2")
    @JsonIgnore
    private List<Chat> chatsReceived;

    // One-to-Many with Orders (as buyer)
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders;

    // One-to-Many with Payments
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Payment> payments;

    // One-to-Many with Messages (sent and received)
    @OneToMany(mappedBy = "sender")
    @JsonIgnore
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver")
    @JsonIgnore
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ProductReview> productReviews;

    // One-to-Many with SellerReviews
    @OneToMany(mappedBy = "reviewer")
    @JsonIgnore
    private List<SellerReview> writtenSellerReviews;

    @OneToMany(mappedBy = "seller")
    @JsonIgnore
    private List<SellerReview> receivedSellerReviews;
    // One-to-Many with stores
    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private List<Store> stores;

    // One-to-Many with Complaints
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Complaint> complaints;

    // One-to-Many with Notifications0
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Notification> notifications;

    // One-to-Many with Subscriptions
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Subscription> subscriptions;

    // One-to-Many with BannedUsers
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<BannedUser> bannedUsers;
}

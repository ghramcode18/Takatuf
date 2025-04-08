package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "banned_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class BannedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String reason;
    private LocalDateTime bannedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}

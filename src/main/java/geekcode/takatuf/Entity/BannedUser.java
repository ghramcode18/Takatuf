package geekcode.takatuf.Entity;

import java.sql.Date;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.UUID;
import java.sql.Timestamp;
import java.util.List;

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
    private Timestamp bannedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}

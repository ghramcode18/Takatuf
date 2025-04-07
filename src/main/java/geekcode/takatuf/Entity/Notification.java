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
@Table(name = "notifications")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String message;
    private Timestamp timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}

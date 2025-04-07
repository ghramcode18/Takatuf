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
@Table(name = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String description;
    private String status;
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}

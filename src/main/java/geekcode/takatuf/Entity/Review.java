package geekcode.takatuf.Entity;

import java.sql.Date;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.UUID;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Integer rating;
    private String comment;
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}

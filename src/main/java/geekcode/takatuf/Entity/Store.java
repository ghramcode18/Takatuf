package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private User ownerId;

    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "store")
    private List<Product> products;
}

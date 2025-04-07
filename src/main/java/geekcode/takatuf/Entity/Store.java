package geekcode.takatuf.Entity;

import java.sql.Date;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.UUID;
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
    private Long ownerId;
    private String name;
    private String description;
    private String status;
    private Timestamp createdAt;

    @OneToMany(mappedBy = "store")
    private List<Product> products;
}

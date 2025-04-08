package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String piece;
    private String image;
    private String category;
    private String color;
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private Store store;
}
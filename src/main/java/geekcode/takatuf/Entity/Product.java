package geekcode.takatuf.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import geekcode.takatuf.Enums.ProductCategory;

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
    private BigDecimal price;
    private String image;

    private Long Quantity;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product")
    private List<ProductReview> productReviews;

    @ManyToOne
    @JoinColumn(name = "store_id")
    @JsonIgnoreProperties
    private Store store;
}
package geekcode.takatuf.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "image_url")
    private String image;

    private Boolean active;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<SellerCategory> sellerCategories;
}

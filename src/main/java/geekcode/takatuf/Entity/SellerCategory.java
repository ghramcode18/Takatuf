package geekcode.takatuf.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime createdAt;
}

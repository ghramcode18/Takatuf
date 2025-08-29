package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "section_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = true)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
